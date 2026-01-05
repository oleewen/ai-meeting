#!/usr/bin/env bash

# Summarize user stories from spec.md by aggregate root
#
# This script extracts user stories from spec.md and groups them by aggregate root
# (key entities) for archival purposes.
#
# Usage: ./summarize-spec.sh [OPTIONS] <spec_file>
#
# OPTIONS:
#   --json              Output in JSON format
#   --output <file>     Output summary to file (default: stdout)
#   --output-dir <dir>  Output directory for split files
#   --split-by-aggregate  Split output by aggregate root into separate files
#   --help, -h          Show help message
#
# OUTPUTS:
#   JSON mode: {"aggregate_roots": [...], "user_stories": [...]}
#   Text mode: Markdown summary grouped by aggregate root

set -e

# Parse command line arguments
JSON_MODE=false
OUTPUT_FILE=""
OUTPUT_DIR=""
SPLIT_BY_AGGREGATE=false
SPEC_FILE=""

for arg in "$@"; do
    case "$arg" in
        --json)
            JSON_MODE=true
            ;;
        --output)
            OUTPUT_FILE_NEXT=true
            ;;
        --output-dir)
            OUTPUT_DIR_NEXT=true
            ;;
        --split-by-aggregate)
            SPLIT_BY_AGGREGATE=true
            ;;
        --help|-h)
            cat << 'EOF'
Usage: summarize-spec.sh [OPTIONS] <spec_file>

Summarize user stories from spec.md by aggregate root.

OPTIONS:
  --json                    Output in JSON format
  --output <file>           Output summary to file (default: stdout)
  --output-dir <dir>        Output directory for split files (requires --split-by-aggregate)
  --split-by-aggregate      Split output by aggregate root into separate files
  --help, -h                Show this help message

EXAMPLES:
  # Summarize user stories (JSON output)
  ./summarize-spec.sh --json spec.md
  
  # Output to file
  ./summarize-spec.sh --output summary.md spec.md
  
  # Split by aggregate root to separate files
  ./summarize-spec.sh --split-by-aggregate --output-dir story/ spec.md
  
EOF
            exit 0
            ;;
        *)
            if [[ "$OUTPUT_FILE_NEXT" == "true" ]]; then
                OUTPUT_FILE="$arg"
                OUTPUT_FILE_NEXT=false
            elif [[ "$OUTPUT_DIR_NEXT" == "true" ]]; then
                OUTPUT_DIR="$arg"
                OUTPUT_DIR_NEXT=false
            elif [[ -z "$SPEC_FILE" ]]; then
                SPEC_FILE="$arg"
            else
                echo "ERROR: Unknown argument '$arg'. Use --help for usage information." >&2
                exit 1
            fi
            ;;
    esac
done

# Validate split mode
if [[ "$SPLIT_BY_AGGREGATE" == "true" ]] && [[ -z "$OUTPUT_DIR" ]]; then
    echo "ERROR: --output-dir is required when using --split-by-aggregate" >&2
    exit 1
fi

# Validate spec file
if [[ -z "$SPEC_FILE" ]]; then
    echo "ERROR: spec file not provided" >&2
    exit 1
fi

if [[ ! -f "$SPEC_FILE" ]]; then
    echo "ERROR: spec file not found: $SPEC_FILE" >&2
    exit 1
fi

# Extract feature information from spec.md
FEATURE_NAME=$(grep -m 1 "^# 功能规格：" "$SPEC_FILE" 2>/dev/null | sed 's/^# 功能规格：//' | sed 's/^[[:space:]]*//' || echo "Unknown Feature")
FEATURE_BRANCH=$(grep -m 1 "^\*\*功能分支\*\*:" "$SPEC_FILE" 2>/dev/null | sed 's/^\*\*功能分支\*\*:[[:space:]]*\`//' | sed 's/\`.*//' || echo "")

# Extract key entities (aggregate roots) from spec.md
# Look for "### 关键实体" section
KEY_ENTITIES=()
if grep -q "### 关键实体" "$SPEC_FILE"; then
    # Extract entity names (lines starting with "- **")
    while IFS= read -r line; do
        if [[ "$line" =~ ^-[[:space:]]+\*\*([^*]+)\*\*: ]]; then
            KEY_ENTITIES+=("${BASH_REMATCH[1]}")
        fi
    done < <(sed -n '/### 关键实体/,/^## /p' "$SPEC_FILE" | grep "^- \*\*")
fi

# If no key entities found, try to infer from user stories
if [[ ${#KEY_ENTITIES[@]} -eq 0 ]]; then
    # Common aggregate roots in domain-driven design
    KEY_ENTITIES=("会议室" "预约" "用户")
fi

# Extract user stories from spec.md with complete content
# User stories are in format: "### 用户故事 N - [Title] (优先级: P1)"
USER_STORIES=()
CURRENT_STORY=""
CURRENT_STORY_CONTENT=""
IN_STORY=false
IN_USER_STORIES_SECTION=false

while IFS= read -r line || [[ -n "$line" ]]; do
    # Check if we're entering user stories section
    if [[ "$line" =~ ^##[[:space:]]+用户场景与测试 ]]; then
        IN_USER_STORIES_SECTION=true
        continue
    fi
    
    # Check if we're leaving user stories section
    if [[ "$IN_USER_STORIES_SECTION" == "true" ]] && [[ "$line" =~ ^##[[:space:]]+需求 ]]; then
        IN_USER_STORIES_SECTION=false
        # Save last story
        if [[ -n "$CURRENT_STORY" ]]; then
            CURRENT_STORY="${CURRENT_STORY}|STORY_CONTENT=${CURRENT_STORY_CONTENT}"
            USER_STORIES+=("$CURRENT_STORY")
            CURRENT_STORY=""
            CURRENT_STORY_CONTENT=""
        fi
        break
    fi
    
    # Only process if we're in user stories section
    if [[ "$IN_USER_STORIES_SECTION" != "true" ]]; then
        continue
    fi
    
    # Check if this is a user story header
    if [[ "$line" =~ ^###[[:space:]]+用户故事[[:space:]]+([0-9]+)[[:space:]]*-[[:space:]]*(.+)[[:space:]]*\(优先级:[[:space:]]*(P[0-9]+)\) ]]; then
        # Save previous story if exists
        if [[ -n "$CURRENT_STORY" ]]; then
            CURRENT_STORY="${CURRENT_STORY}|STORY_CONTENT=${CURRENT_STORY_CONTENT}"
            USER_STORIES+=("$CURRENT_STORY")
        fi
        
        STORY_NUM="${BASH_REMATCH[1]}"
        STORY_TITLE="${BASH_REMATCH[2]}"
        STORY_PRIORITY="${BASH_REMATCH[3]}"
        
        CURRENT_STORY="STORY_NUM=$STORY_NUM|STORY_TITLE=$STORY_TITLE|STORY_PRIORITY=$STORY_PRIORITY|STORY_DESC="
        CURRENT_STORY_CONTENT=""
        IN_STORY=true
        continue
    fi
    
    # If we're in a story, collect all content until next story or end marker
    if [[ "$IN_STORY" == "true" ]]; then
        # Check if we hit next story or end marker (---)
        if [[ "$line" =~ ^--- ]]; then
            IN_STORY=false
            continue
        fi
        
        # Collect description line (starts with "作为")
        if [[ "$line" =~ ^作为 ]]; then
            DESC=$(echo "$line" | sed 's/^[[:space:]]*//' | sed 's/[[:space:]]*$//')
            CURRENT_STORY="${CURRENT_STORY}${DESC}"
        fi
        
        # Collect all content (including 优先级理由、独立测试、验收场景等)
        # Use a delimiter that won't appear in content (|||)
        CURRENT_STORY_CONTENT="${CURRENT_STORY_CONTENT}${line}|||"
    fi
done < "$SPEC_FILE"

# Save last story
if [[ -n "$CURRENT_STORY" ]]; then
    CURRENT_STORY="${CURRENT_STORY}|STORY_CONTENT=${CURRENT_STORY_CONTENT}"
    USER_STORIES+=("$CURRENT_STORY")
fi

# Map user stories to aggregate roots
# For each story, determine which aggregate roots it relates to
declare -A AGGREGATE_STORIES

for entity in "${KEY_ENTITIES[@]}"; do
    AGGREGATE_STORIES["$entity"]=""
done

for story_data in "${USER_STORIES[@]}"; do
    # Extract story information
    STORY_NUM=$(echo "$story_data" | grep -oP 'STORY_NUM=\K[^|]+')
    STORY_TITLE=$(echo "$story_data" | grep -oP 'STORY_TITLE=\K[^|]+')
    STORY_DESC=$(echo "$story_data" | grep -oP 'STORY_DESC=\K[^|]+')
    
    # Determine which aggregate roots this story relates to
    # Check if story title or description mentions any entity
    STORY_TEXT="$STORY_TITLE $STORY_DESC"
    
    for entity in "${KEY_ENTITIES[@]}"; do
        if echo "$STORY_TEXT" | grep -qi "$entity"; then
            if [[ -z "${AGGREGATE_STORIES[$entity]}" ]]; then
                AGGREGATE_STORIES["$entity"]="$story_data"
            else
                AGGREGATE_STORIES["$entity"]="${AGGREGATE_STORIES[$entity]}|$story_data"
            fi
        fi
    done
done

# Function to check if user story exists in file (by title)
story_exists_in_file() {
    local file="$1"
    local story_title="$2"
    
    if [[ ! -f "$file" ]]; then
        return 1
    fi
    
    # Check if file contains user story with this title
    # Pattern: "### 用户故事 N - {title} (优先级: PX)"
    # Escape special regex characters in title
    local escaped_title=$(echo "$story_title" | sed 's/[[\.*^$()+?{|]/\\&/g')
    if grep -q "### 用户故事.*-.*${escaped_title}.*(优先级:" "$file" 2>/dev/null; then
        return 0
    fi
    
    return 1
}

# Function to merge user story into existing file (update existing story)
merge_story_into_file() {
    local file="$1"
    local story_title="$2"
    local story_content="$3"
    local story_num="$4"
    local story_priority="$5"
    local feature_branch="$6"
    
    # Escape special regex characters in title
    local escaped_title=$(echo "$story_title" | sed 's/[[\.*^$()+?{|]/\\&/g')
    
    # Create temporary file
    local temp_file=$(mktemp)
    
    local in_target_story=false
    local story_found=false
    local skip_content=false
    
    while IFS= read -r line || [[ -n "$line" ]]; do
        # Check if this is the target user story header
        # Pattern: "### 用户故事 N - {title} (优先级: PX)"
        if [[ "$line" =~ ^###[[:space:]]+用户故事[[:space:]]+[0-9]+[[:space:]]*-[[:space:]]*${escaped_title}[[:space:]]*\(优先级: ]]; then
            in_target_story=true
            story_found=true
            skip_content=true
            # Write updated header with current story number and priority
            echo "### 用户故事 $story_num - $story_title (优先级: $story_priority)" >> "$temp_file"
            continue
        fi
        
        # Skip content until we hit separator (---)
        if [[ "$skip_content" == "true" ]]; then
            if [[ "$line" =~ ^--- ]]; then
                skip_content=false
                in_target_story=false
                # Write new content
                echo "" >> "$temp_file"
                echo "$story_content" | sed 's/|||/\n/g' >> "$temp_file"
                echo "" >> "$temp_file"
                echo "---" >> "$temp_file"
            fi
            continue
        fi
        
        # Write line as-is
        echo "$line" >> "$temp_file"
    done < "$file"
    
    # If story not found, append at end (before source info)
    if [[ "$story_found" == "false" ]]; then
        # Find where source info starts
        local source_line=$(grep -n "^\*\*来源功能分支\*\*" "$temp_file" 2>/dev/null | head -1 | cut -d: -f1 || echo "0")
        
        if [[ "$source_line" -gt 0 ]]; then
            # Insert before source info
            local before_source=$(head -n $((source_line - 1)) "$temp_file")
            local after_source=$(tail -n +$source_line "$temp_file")
            
            {
                echo "$before_source"
                echo ""
                echo "### 用户故事 $story_num - $story_title (优先级: $story_priority)"
                echo ""
                echo "$story_content" | sed 's/|||/\n/g'
                echo ""
                echo "---"
                echo ""
                echo "$after_source"
            } > "$temp_file"
        else
            # Append at end
            echo "" >> "$temp_file"
            echo "### 用户故事 $story_num - $story_title (优先级: $story_priority)" >> "$temp_file"
            echo "" >> "$temp_file"
            echo "$story_content" | sed 's/|||/\n/g' >> "$temp_file"
            echo "" >> "$temp_file"
            echo "---" >> "$temp_file"
        fi
    fi
    
    # Update source info to include current branch if not present
    if ! grep -q "\`${feature_branch}\`" "$temp_file" 2>/dev/null; then
        # Add or update source info
        if grep -q "^\*\*来源功能分支\*\*" "$temp_file" 2>/dev/null; then
            # Update existing source info to include all branches
            local existing_branches=$(grep "^\*\*来源功能分支\*\*" "$temp_file" | sed 's/^\*\*来源功能分支\*\*:[[:space:]]*//' | sed 's/\`//g' || echo "")
            if [[ -n "$existing_branches" ]] && [[ "$existing_branches" != *"${feature_branch}"* ]]; then
                # Append new branch
                sed -i.bak "s|^\*\*来源功能分支\*\*:.*|\*\*来源功能分支\*\*: ${existing_branches}, \`${feature_branch}\`|" "$temp_file" 2>/dev/null || \
                sed -i '' "s|^\*\*来源功能分支\*\*:.*|\*\*来源功能分支\*\*: ${existing_branches}, \`${feature_branch}\`|" "$temp_file" 2>/dev/null
                rm -f "$temp_file.bak" 2>/dev/null || true
            fi
        else
            # Add source info at end
            echo "" >> "$temp_file"
            echo "**来源功能分支**: \`${feature_branch}\`" >> "$temp_file"
            echo "**归档日期**: $(date +"%Y-%m-%d")" >> "$temp_file"
        fi
    fi
    
    # Replace original file
    mv "$temp_file" "$file"
}

# Generate summary
if $SPLIT_BY_AGGREGATE; then
    # Split by aggregate root - generate one file per aggregate
    if [[ -n "$OUTPUT_DIR" ]]; then
        mkdir -p "$OUTPUT_DIR"
        
        for entity in "${KEY_ENTITIES[@]}"; do
            # Create filename: AGGREGATE-spec.md (e.g., 会议室-spec.md)
            ENTITY_FILE="$OUTPUT_DIR/${entity}-spec.md"
            
            # Check if file exists
            FILE_EXISTS=false
            if [[ -f "$ENTITY_FILE" ]]; then
                FILE_EXISTS=true
            fi
            
            IFS='|' read -ra STORIES <<< "${AGGREGATE_STORIES[$entity]}"
            if [[ ${#STORIES[@]} -eq 0 ]] || [[ -z "${STORIES[0]}" ]]; then
                if [[ "$FILE_EXISTS" == "false" ]]; then
                    # Create empty file with header
                    ENTITY_SUMMARY="# ${entity} 相关用户故事\n\n"
                    ENTITY_SUMMARY+="**功能**: $FEATURE_NAME\n"
                    ENTITY_SUMMARY+="**功能分支**: $FEATURE_BRANCH\n"
                    ENTITY_SUMMARY+="**汇总日期**: $(date +"%Y-%m-%d")\n\n"
                    ENTITY_SUMMARY+="## 用户故事列表\n\n"
                    ENTITY_SUMMARY+="*暂无相关用户故事*\n"
                    echo -e "$ENTITY_SUMMARY" > "$ENTITY_FILE"
                fi
            else
                # Process each story - check for duplicates and merge/append
                local new_stories=()
                local updated_stories=()
                
                for story_data in "${STORIES[@]}"; do
                    if [[ -n "$story_data" ]]; then
                        STORY_NUM=$(echo "$story_data" | grep -oP 'STORY_NUM=\K[^|]+')
                        STORY_TITLE=$(echo "$story_data" | grep -oP 'STORY_TITLE=\K[^|]+')
                        STORY_PRIORITY=$(echo "$story_data" | grep -oP 'STORY_PRIORITY=\K[^|]+')
                        STORY_DESC=$(echo "$story_data" | grep -oP 'STORY_DESC=\K[^|]+')
                        STORY_CONTENT=$(echo "$story_data" | grep -oP 'STORY_CONTENT=\K.*' || echo "")
                        
                        # Check if story already exists (by title)
                        if [[ "$FILE_EXISTS" == "true" ]] && story_exists_in_file "$ENTITY_FILE" "$STORY_TITLE"; then
                            # Story exists, merge/update it (FR-019)
                            merge_story_into_file "$ENTITY_FILE" "$STORY_TITLE" "$STORY_CONTENT" "$STORY_NUM" "$STORY_PRIORITY" "$FEATURE_BRANCH"
                            updated_stories+=("$STORY_TITLE")
                        else
                            # New story, add to list for appending
                            new_stories+=("$story_data")
                        fi
                    fi
                done
                
                # Append new stories to file
                if [[ ${#new_stories[@]} -gt 0 ]]; then
                    local append_content=""
                    
                    # If file doesn't exist, create header
                    if [[ "$FILE_EXISTS" == "false" ]]; then
                        append_content="# ${entity} 相关用户故事\n\n"
                        append_content+="**功能**: $FEATURE_NAME\n"
                        append_content+="**功能分支**: $FEATURE_BRANCH\n"
                        append_content+="**汇总日期**: $(date +"%Y-%m-%d")\n\n"
                        append_content+="## 用户故事列表\n\n"
                    else
                        # Remove last source info lines for appending
                        local temp_file=$(mktemp)
                        local source_line=$(grep -n "^\*\*来源功能分支\*\*" "$ENTITY_FILE" 2>/dev/null | head -1 | cut -d: -f1 || echo "0")
                        if [[ "$source_line" -gt 0 ]]; then
                            head -n $((source_line - 1)) "$ENTITY_FILE" > "$temp_file"
                            mv "$temp_file" "$ENTITY_FILE"
                        fi
                    fi
                    
                    # Add new stories
                    for story_data in "${new_stories[@]}"; do
                        STORY_NUM=$(echo "$story_data" | grep -oP 'STORY_NUM=\K[^|]+')
                        STORY_TITLE=$(echo "$story_data" | grep -oP 'STORY_TITLE=\K[^|]+')
                        STORY_PRIORITY=$(echo "$story_data" | grep -oP 'STORY_PRIORITY=\K[^|]+')
                        STORY_CONTENT=$(echo "$story_data" | grep -oP 'STORY_CONTENT=\K.*' || echo "")
                        
                        append_content+="### 用户故事 $STORY_NUM - $STORY_TITLE (优先级: $STORY_PRIORITY)\n\n"
                        if [[ -n "$STORY_CONTENT" ]]; then
                            STORY_CONTENT=$(echo "$STORY_CONTENT" | sed 's/|||/\n/g')
                            append_content+="${STORY_CONTENT}\n\n"
                        else
                            append_content+="$STORY_DESC\n\n"
                        fi
                        append_content+="---\n\n"
                    done
                    
                    # Add source info
                    append_content+="**来源功能分支**: \`$FEATURE_BRANCH\`\n"
                    append_content+="**归档日期**: $(date +"%Y-%m-%d")\n"
                    
                    if [[ "$FILE_EXISTS" == "false" ]]; then
                        echo -e "$append_content" > "$ENTITY_FILE"
                    else
                        echo -e "$append_content" >> "$ENTITY_FILE"
                    fi
                fi
                
                # Output status
                if $JSON_MODE; then
                    printf '{"aggregate":"%s","file":"%s","updated":%d,"added":%d}\n' \
                        "$entity" "$ENTITY_FILE" "${#updated_stories[@]}" "${#new_stories[@]}"
                else
                    if [[ ${#updated_stories[@]} -gt 0 ]]; then
                        echo "Updated ${#updated_stories[@]} story(ies) in: $ENTITY_FILE"
                    fi
                    if [[ ${#new_stories[@]} -gt 0 ]]; then
                        echo "Added ${#new_stories[@]} new story(ies) to: $ENTITY_FILE"
                    fi
                    if [[ ${#updated_stories[@]} -eq 0 ]] && [[ ${#new_stories[@]} -eq 0 ]]; then
                        echo "No changes to: $ENTITY_FILE"
                    fi
                fi
            fi
        done
    fi
elif $JSON_MODE; then
    # JSON output
    printf '{"feature_name":"%s","feature_branch":"%s","aggregate_roots":[' "$FEATURE_NAME" "$FEATURE_BRANCH"
    
    FIRST=true
    for entity in "${KEY_ENTITIES[@]}"; do
        if [[ "$FIRST" == "true" ]]; then
            FIRST=false
        else
            printf ","
        fi
        
        printf '{"name":"%s","stories":[' "$entity"
        
        STORY_FIRST=true
        IFS='|' read -ra STORIES <<< "${AGGREGATE_STORIES[$entity]}"
        for story_data in "${STORIES[@]}"; do
            if [[ -n "$story_data" ]]; then
                if [[ "$STORY_FIRST" == "true" ]]; then
                    STORY_FIRST=false
                else
                    printf ","
                fi
                
                STORY_NUM=$(echo "$story_data" | grep -oP 'STORY_NUM=\K[^|]+')
                STORY_TITLE=$(echo "$story_data" | grep -oP 'STORY_TITLE=\K[^|]+')
                STORY_PRIORITY=$(echo "$story_data" | grep -oP 'STORY_PRIORITY=\K[^|]+')
                STORY_DESC=$(echo "$story_data" | grep -oP 'STORY_DESC=\K[^|]+')
                STORY_CONTENT=$(echo "$story_data" | grep -oP 'STORY_CONTENT=\K.*' || echo "")
                
                # Escape JSON special characters
                STORY_CONTENT_ESC=$(echo "$STORY_CONTENT" | sed 's/\\/\\\\/g' | sed 's/"/\\"/g' | sed 's/|||/\\n/g')
                
                printf '{"number":"%s","title":"%s","priority":"%s","description":"%s","content":"%s"}' \
                    "$STORY_NUM" "$STORY_TITLE" "$STORY_PRIORITY" "$STORY_DESC" "$STORY_CONTENT_ESC"
            fi
        done
        
        printf "]}"
    done
    
    printf "]}"
else
    # Markdown output
    SUMMARY="# 用户故事汇总：$FEATURE_NAME\n\n"
    SUMMARY+="**功能分支**: $FEATURE_BRANCH\n"
    SUMMARY+="**汇总日期**: $(date +"%Y-%m-%d")\n\n"
    SUMMARY+="## 按聚合根汇总\n\n"
    
    for entity in "${KEY_ENTITIES[@]}"; do
        SUMMARY+="### $entity\n\n"
        
        IFS='|' read -ra STORIES <<< "${AGGREGATE_STORIES[$entity]}"
        if [[ ${#STORIES[@]} -eq 0 ]] || [[ -z "${STORIES[0]}" ]]; then
            SUMMARY+="*暂无相关用户故事*\n\n"
        else
            for story_data in "${STORIES[@]}"; do
                if [[ -n "$story_data" ]]; then
                    STORY_NUM=$(echo "$story_data" | grep -oP 'STORY_NUM=\K[^|]+')
                    STORY_TITLE=$(echo "$story_data" | grep -oP 'STORY_TITLE=\K[^|]+')
                    STORY_PRIORITY=$(echo "$story_data" | grep -oP 'STORY_PRIORITY=\K[^|]+')
                    STORY_DESC=$(echo "$story_data" | grep -oP 'STORY_DESC=\K[^|]+')
                    STORY_CONTENT=$(echo "$story_data" | grep -oP 'STORY_CONTENT=\K.*' || echo "")
                    
                    SUMMARY+="#### 用户故事 $STORY_NUM - $STORY_TITLE (优先级: $STORY_PRIORITY)\n\n"
                    # Output complete user story content
                    if [[ -n "$STORY_CONTENT" ]]; then
                        STORY_CONTENT=$(echo "$STORY_CONTENT" | sed 's/|||/\n/g')
                        STORY_CONTENT=$(echo -e "$STORY_CONTENT" | sed 's/\n$//')
                        SUMMARY+="${STORY_CONTENT}\n\n"
                    else
                        SUMMARY+="$STORY_DESC\n\n"
                    fi
                fi
            done
        fi
        SUMMARY+="---\n\n"
    done
    
    # Output to file or stdout
    if [[ -n "$OUTPUT_FILE" ]]; then
        echo -e "$SUMMARY" > "$OUTPUT_FILE"
        echo "Summary written to: $OUTPUT_FILE"
    else
        echo -e "$SUMMARY"
    fi
fi
