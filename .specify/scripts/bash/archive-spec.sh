#!/usr/bin/env bash

# Archive feature specification directory to specs/archive
#
# This script moves a feature specification directory to specs/archive and
# extracts user stories grouped by aggregate root.
#
# Usage: ./archive-spec.sh [OPTIONS]
#
# OPTIONS:
#   --json              Output in JSON format
#   --branch <name>     Specify feature branch name manually
#   --dir <path>        Specify specification directory path manually
#   --dry-run           Show what would be done without actually doing it
#   --yes               Auto-confirm all prompts (non-interactive mode)
#   --help, -h          Show help message
#
# OUTPUTS:
#   JSON mode: {"status": "success", "archive_dir": "...", "aggregate_files": [...]}
#   Text mode: Progress messages and summary

set -e

# 脚本目录和通用函数
SCRIPT_DIR="$(CDPATH="" cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$SCRIPT_DIR/common.sh"

# 全局变量
JSON_MODE=false
DRY_RUN=false
AUTO_YES=false
MANUAL_BRANCH=""
MANUAL_DIR=""
REPO_ROOT=$(get_repo_root)
CURRENT_BRANCH=$(get_current_branch)
FEATURE_DIR=""
ARCHIVE_DIR=""
SPEC_FILE=""
ROLLBACK_STATE=""
AGGREGATE_ROOTS=()

# 解析命令行参数
parse_arguments() {
    while [[ $# -gt 0 ]]; do
        case "$1" in
            --json)
                JSON_MODE=true
                shift
                ;;
            --branch)
                if [[ -z "$2" ]]; then
                    echo "ERROR: --branch requires a value" >&2
                    exit 1
                fi
                MANUAL_BRANCH="$2"
                shift 2
                ;;
            --dir)
                if [[ -z "$2" ]]; then
                    echo "ERROR: --dir requires a value" >&2
                    exit 1
                fi
                MANUAL_DIR="$2"
                shift 2
                ;;
            --dry-run)
                DRY_RUN=true
                shift
                ;;
            --yes)
                AUTO_YES=true
                shift
                ;;
            --help|-h)
                show_help
                exit 0
                ;;
            *)
                echo "ERROR: Unknown argument '$1'. Use --help for usage information." >&2
                exit 1
                ;;
        esac
    done
}

# 显示帮助信息
show_help() {
    cat << 'EOF'
Usage: archive-spec.sh [OPTIONS]

Archive feature specification directory to specs/archive.

OPTIONS:
  --json                    Output in JSON format
  --branch <name>           Specify feature branch name manually (FR-015)
  --dir <path>              Specify specification directory path manually (FR-015)
  --dry-run                 Show what would be done without actually doing it
  --yes                     Auto-confirm all prompts (non-interactive mode)
  --help, -h                Show this help message

EXAMPLES:
  # Archive current feature branch
  ./archive-spec.sh
  
  # Archive with manual branch name
  ./archive-spec.sh --branch 001-feature-name
  
  # Archive with manual directory path
  ./archive-spec.sh --dir specs/001-feature-name
  
  # Dry run to see what would happen
  ./archive-spec.sh --dry-run
  
  # Non-interactive mode
  ./archive-spec.sh --yes

EOF
}

# 日志输出函数
log_progress() {
    local level="$1"
    shift
    local message="$*"
    
    if [[ "$JSON_MODE" == "true" ]]; then
        # JSON 模式下，日志输出到 stderr
        echo "{\"level\":\"$level\",\"message\":\"$message\"}" >&2
    else
        case "$level" in
            INFO)
                echo "[INFO] $message"
                ;;
            WARN)
                echo "[WARN] $message" >&2
                ;;
            ERROR)
                echo "[ERROR] $message" >&2
                ;;
            SUCCESS)
                echo "[SUCCESS] $message"
                ;;
        esac
    fi
}

# 获取功能分支名称（FR-014）
get_feature_branch_name() {
    if [[ -n "$MANUAL_BRANCH" ]]; then
        echo "$MANUAL_BRANCH"
        return
    fi
    
    if [[ -n "$MANUAL_DIR" ]]; then
        # 从目录路径提取分支名
        local dirname=$(basename "$MANUAL_DIR")
        echo "$dirname"
        return
    fi
    
    # 从当前 git 分支自动识别（FR-014）
    if [[ -n "$CURRENT_BRANCH" ]] && [[ "$CURRENT_BRANCH" =~ ^[0-9]{3}- ]]; then
        echo "$CURRENT_BRANCH"
        return
    fi
    
    # 如果无法识别，使用环境变量
    if [[ -n "${SPECIFY_FEATURE:-}" ]]; then
        echo "$SPECIFY_FEATURE"
        return
    fi
    
    echo ""
}

# 获取规格目录路径
get_feature_directory() {
    if [[ -n "$MANUAL_DIR" ]]; then
        if [[ -d "$MANUAL_DIR" ]]; then
            echo "$MANUAL_DIR"
            return
        else
            log_progress ERROR "指定的目录不存在: $MANUAL_DIR"
            exit 1
        fi
    fi
    
    local branch_name=$(get_feature_branch_name)
    if [[ -z "$branch_name" ]]; then
        log_progress ERROR "无法识别功能分支名称。请使用 --branch 或 --dir 参数手动指定。"
        exit 1
    fi
    
    local feature_dir="$REPO_ROOT/specs/$branch_name"
    echo "$feature_dir"
}

# 验证规格目录（FR-002）
validate_spec_directory() {
    local dir="$1"
    
    if [[ ! -d "$dir" ]]; then
        log_progress ERROR "规格目录不存在: $dir"
        exit 1
    fi
    
    local spec_file="$dir/spec.md"
    if [[ ! -f "$spec_file" ]]; then
        log_progress ERROR "规格目录中缺少 spec.md 文件: $dir"
        exit 1
    fi
    
    log_progress INFO "验证规格目录成功: $dir"
}

# 处理归档目录冲突（FR-012）
handle_archive_conflict() {
    local archive_path="$1"
    
    if [[ -d "$archive_path" ]]; then
        if [[ "$AUTO_YES" == "true" ]]; then
            # 非交互模式：自动添加时间戳后缀
            local timestamp=$(date +"%Y%m%d%H%M%S")
            archive_path="${archive_path}.${timestamp}"
            log_progress WARN "目标目录已存在，自动添加时间戳后缀: $archive_path"
        else
            # 交互模式：询问用户
            echo "目标归档目录已存在: $archive_path"
            echo "请选择处理方式："
            echo "  1) 覆盖现有目录"
            echo "  2) 添加时间戳后缀"
            echo "  3) 取消操作"
            read -p "请输入选项 (1/2/3): " choice
            
            case "$choice" in
                1)
                    log_progress WARN "将覆盖现有目录: $archive_path"
                    ;;
                2)
                    local timestamp=$(date +"%Y%m%d%H%M%S")
                    archive_path="${archive_path}.${timestamp}"
                    log_progress INFO "使用时间戳后缀: $archive_path"
                    ;;
                3)
                    log_progress INFO "操作已取消"
                    exit 0
                    ;;
                *)
                    log_progress ERROR "无效的选项"
                    exit 1
                    ;;
            esac
        fi
    fi
    
    echo "$archive_path"
}

# 保存回滚状态
save_rollback_state() {
    ROLLBACK_STATE="source:$FEATURE_DIR|target:$ARCHIVE_DIR"
}

# 回滚归档操作（FR-013）
rollback_archive() {
    if [[ -z "$ROLLBACK_STATE" ]]; then
        return
    fi
    
    log_progress WARN "开始回滚归档操作..."
    
    # 解析回滚状态
    local source_dir=$(echo "$ROLLBACK_STATE" | grep -oP 'source:\K[^|]+')
    local target_dir=$(echo "$ROLLBACK_STATE" | grep -oP 'target:\K[^|]+')
    
    # 如果目标目录存在，移回原位置
    if [[ -d "$target_dir" ]] && [[ ! -d "$source_dir" ]]; then
        if [[ "$DRY_RUN" == "true" ]]; then
            log_progress INFO "[DRY-RUN] 将移动 $target_dir 回 $source_dir"
        else
            mv "$target_dir" "$source_dir"
            log_progress INFO "已回滚：目录已移回原位置"
        fi
    fi
    
    ROLLBACK_STATE=""
}

# 移动规格目录到归档目录（FR-001, FR-003）
move_spec_to_archive() {
    local source_dir="$1"
    local target_dir="$2"
    
    # 确保 archive 目录存在
    local archive_base=$(dirname "$target_dir")
    if [[ ! -d "$archive_base" ]]; then
        if [[ "$DRY_RUN" == "true" ]]; then
            log_progress INFO "[DRY-RUN] 将创建目录: $archive_base"
        else
            mkdir -p "$archive_base"
            log_progress INFO "已创建归档基础目录: $archive_base"
        fi
    fi
    
    # 保存回滚状态
    save_rollback_state
    
    # 执行移动操作
    if [[ "$DRY_RUN" == "true" ]]; then
        log_progress INFO "[DRY-RUN] 将移动 $source_dir 到 $target_dir"
    else
        mv "$source_dir" "$target_dir"
        log_progress SUCCESS "已移动规格目录到归档位置: $target_dir"
    fi
}

# 删除原目录（FR-001）
remove_original_directory() {
    local dir="$1"
    
    if [[ -d "$dir" ]]; then
        if [[ "$DRY_RUN" == "true" ]]; then
            log_progress INFO "[DRY-RUN] 将删除目录: $dir"
        else
            rm -rf "$dir"
            log_progress SUCCESS "已删除原规格目录: $dir"
        fi
    fi
}

# 提取聚合根列表（FR-006）
extract_aggregate_roots() {
    local spec_file="$1"
    local entities=()
    
    if grep -q "### 关键实体" "$spec_file"; then
        while IFS= read -r line; do
            if [[ "$line" =~ ^-[[:space:]]+\*\*([^*]+)\*\*: ]]; then
                entities+=("${BASH_REMATCH[1]}")
            fi
        done < <(sed -n '/### 关键实体/,/^## /p' "$spec_file" | grep "^- \*\*")
    fi
    
    # 输出为数组（通过全局变量返回）
    AGGREGATE_ROOTS=("${entities[@]}")
}

# 判定聚合根归属（FR-007）
determine_aggregate_root() {
    local story_title="$1"
    local story_desc="$2"
    local story_text="$story_title $story_desc"
    local matched_entities=()
    
    # 优先选择"关键实体"章节中列出的聚合根
    for entity in "${AGGREGATE_ROOTS[@]}"; do
        if echo "$story_text" | grep -qi "$entity"; then
            matched_entities+=("$entity")
        fi
    done
    
    # 如果匹配到多个关键实体，选择操作动词直接作用的对象
    # 这里使用简单的启发式：选择在"我希望"之后出现的第一个实体
    if [[ ${#matched_entities[@]} -gt 1 ]]; then
        # 查找"我希望"之后的内容
        local after_wish=$(echo "$story_desc" | sed -n 's/.*我希望\(.*\)/\1/p')
        for entity in "${matched_entities[@]}"; do
            if echo "$after_wish" | grep -qi "$entity"; then
                echo "$entity"
                return
            fi
        done
        # 如果找不到，返回第一个匹配的
        echo "${matched_entities[0]}"
        return
    elif [[ ${#matched_entities[@]} -eq 1 ]]; then
        echo "${matched_entities[0]}"
        return
    fi
    
    # 如果无法匹配，使用启发式规则（FR-005）
    apply_heuristic_rules "$story_text"
}

# 应用启发式规则（FR-005）
apply_heuristic_rules() {
    local story_text="$1"
    
    # 统计实体名词出现频率
    declare -A entity_count
    for entity in "${AGGREGATE_ROOTS[@]}"; do
        local count=$(echo "$story_text" | grep -oi "$entity" | wc -l)
        if [[ $count -gt 0 ]]; then
            entity_count["$entity"]=$count
        fi
    done
    
    # 选择出现频率最高的实体
    local max_count=0
    local selected_entity=""
    for entity in "${!entity_count[@]}"; do
        if [[ ${entity_count[$entity]} -gt $max_count ]]; then
            max_count=${entity_count[$entity]}
            selected_entity="$entity"
        fi
    done
    
    if [[ -n "$selected_entity" ]]; then
        echo "$selected_entity"
    else
        # 如果仍然无法确定，返回空（将由调用者处理）
        echo ""
    fi
}

# 生成聚合根规格文件（FR-009, FR-010, FR-011, FR-018, FR-019）
generate_aggregate_spec_files() {
    local archive_dir="$1"
    local spec_file="$2"
    local branch_name=$(get_feature_branch_name)
    
    # 提取功能信息
    local feature_name=$(grep -m 1 "^# 功能规格：" "$spec_file" 2>/dev/null | sed 's/^# 功能规格：//' | sed 's/^[[:space:]]*//' || echo "Unknown Feature")
    
    # 提取聚合根列表
    extract_aggregate_roots "$spec_file"
    
    # 创建聚合根规格文件目录（统一放在 archive/spec 目录下）
    local spec_dir="$REPO_ROOT/specs/archive/spec"
    if [[ "$DRY_RUN" == "true" ]]; then
        log_progress INFO "[DRY-RUN] 将创建目录: $spec_dir"
    else
        mkdir -p "$spec_dir"
        log_progress INFO "已创建聚合根规格文件目录: $spec_dir"
    fi
    
    # 调用 summarize-spec.sh 获取用户故事数据（JSON格式）
    local summarize_script="$SCRIPT_DIR/summarize-spec.sh"
    if [[ ! -f "$summarize_script" ]]; then
        log_progress WARN "summarize-spec.sh 不存在，跳过用户故事提取"
        return
    fi
    
    # 使用 summarize-spec.sh 的 --split-by-aggregate 模式生成文件
    # summarize-spec.sh 会自动处理：提取完整内容、去重、合并、添加来源信息
    if [[ "$DRY_RUN" == "true" ]]; then
        log_progress INFO "[DRY-RUN] 将调用 summarize-spec.sh 生成聚合根规格文件"
    else
        local output=$(mktemp)
        "$summarize_script" --split-by-aggregate --output-dir "$spec_dir" "$spec_file" > "$output" 2>&1 || {
            log_progress WARN "用户故事提取失败，但归档操作已完成"
            rm -f "$output"
            return
        }
        
        # 显示处理结果
        if [[ -s "$output" ]]; then
            while IFS= read -r line; do
                if [[ -n "$line" ]]; then
                    log_progress INFO "$line"
                fi
            done < "$output"
        fi
        rm -f "$output"
        
        log_progress SUCCESS "已生成聚合根规格文件到: $spec_dir"
    fi
}

# 归档主函数
archive_spec_directory() {
    # 获取路径
    FEATURE_DIR=$(get_feature_directory)
    validate_spec_directory "$FEATURE_DIR"
    
    SPEC_FILE="$FEATURE_DIR/spec.md"
    local branch_name=$(get_feature_branch_name)
    ARCHIVE_DIR="$REPO_ROOT/specs/archive/$branch_name"
    
    # 处理冲突
    ARCHIVE_DIR=$(handle_archive_conflict "$ARCHIVE_DIR")
    
    # 执行移动
    move_spec_to_archive "$FEATURE_DIR" "$ARCHIVE_DIR"
    
    # 生成聚合根规格文件（用户故事2和3）
    if [[ "$DRY_RUN" != "true" ]] && [[ -d "$ARCHIVE_DIR" ]]; then
        local archived_spec_file="$ARCHIVE_DIR/spec.md"
        if [[ -f "$archived_spec_file" ]]; then
            generate_aggregate_spec_files "$ARCHIVE_DIR" "$archived_spec_file"
        fi
    fi
    
    log_progress INFO "归档操作完成"
}

# 主函数
main() {
    parse_arguments "$@"
    
    # 设置错误处理
    trap 'rollback_archive; exit 1' ERR
    
    # 执行归档
    archive_spec_directory
    
    log_progress SUCCESS "归档操作成功完成"
}

# 如果直接执行此脚本，运行主函数
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
