---
description: Archive a completed feature specification to specs/archive directory and generate aggregate root spec files.
handoffs: 
  - label: Analyze For Consistency
    agent: speckit.analyze
    prompt: Run a project analysis for consistency
    send: true
---

## User Input

```text
$ARGUMENTS
```

You **MUST** consider the user input before proceeding (if not empty).

## Outline

Archive the current feature specification directory to `specs/archive` and generate aggregate root spec files.

This command:
1. Moves the feature specification directory from `specs/{branch-name}` to `specs/archive/{branch-name}`
2. Extracts user stories from spec.md
3. Groups user stories by aggregate root
4. Generates `{aggregate-root}-spec.md` files in `specs/archive/{branch-name}/spec/`

## Execution Steps

1. **Setup**: Run `.specify/scripts/bash/check-prerequisites.sh --json` from repo root and parse FEATURE_DIR. For single quotes in args like "I'm Groot", use escape syntax: e.g 'I'\''m Groot' (or double-quote if possible: "I'm Groot").

2. **Execute archive script**: Run `.specify/scripts/bash/archive-spec.sh` with appropriate arguments:
   - If user provided `--branch <name>`: Pass `--branch <name>`
   - If user provided `--dir <path>`: Pass `--dir <path>`
   - If user provided `--dry-run`: Pass `--dry-run`
   - If user provided `--yes`: Pass `--yes`
   - Always pass `--json` for structured output

3. **Parse results**: Extract archive directory path and aggregate spec files from JSON output.

4. **Report completion**: Display summary of archived files and generated aggregate root spec files.

## Usage Examples

```bash
# Archive current feature branch
/speckit.archive

# Archive with manual branch name
/speckit.archive --branch 001-feature-name

# Archive with manual directory path
/speckit.archive --dir specs/001-feature-name

# Dry run to see what would happen
/speckit.archive --dry-run

# Non-interactive mode
/speckit.archive --yes
```

## Command Options

- `--branch <name>`: Specify feature branch name manually (FR-015)
- `--dir <path>`: Specify specification directory path manually (FR-015)
- `--dry-run`: Show what would be done without actually doing it
- `--yes`: Auto-confirm all prompts (non-interactive mode)

## Output

The command outputs:
- Archive directory path
- List of generated aggregate root spec files
- Summary of archived user stories
