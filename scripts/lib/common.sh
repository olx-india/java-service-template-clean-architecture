#!/usr/bin/env bash
# Shared helpers for template scripts. Source, don't execute.
#
# Honoured environment variables:
#   DRY_RUN=1  print what would change; touch nothing
#   FORCE=1    run even when the git working tree has uncommitted changes

DRY_RUN="${DRY_RUN:-0}"
FORCE="${FORCE:-0}"

OLD_PACKAGE="com.olx.boilerplate"
OLD_PACKAGE_PATH="${OLD_PACKAGE//.//}"
OLD_ARTIFACT="boilerplate"
OLD_APP="BoilerplateApplication"

die() {
  echo "ERROR: $*" >&2
  exit 1
}

is_dry_run() {
  [[ "$DRY_RUN" == "1" || "$DRY_RUN" == "true" ]]
}

is_forced() {
  [[ "$FORCE" == "1" || "$FORCE" == "true" ]]
}

# Refuse to run on a dirty tree so the change is reviewable with `git diff`.
require_clean_git_tree() {
  is_dry_run && return 0
  is_forced && return 0
  git rev-parse --is-inside-work-tree >/dev/null 2>&1 || return 0
  if [[ -n "$(git status --porcelain)" ]]; then
    die "Git working tree has uncommitted changes. Commit/stash them first, or re-run with FORCE=1."
  fi
}

# Run a mutating command, or just print it in dry-run mode.
run() {
  if is_dry_run; then
    echo "  [dry-run] $*"
  else
    "$@"
  fi
}

# Remove a file or directory if present.
remove_path() {
  local path="$1"
  [[ -e "$path" ]] || return 0
  echo "  - remove  $path"
  is_dry_run || rm -rf "$path"
}

# Write stdin to a file (creating parent dirs). In dry-run, discard stdin.
write_file() {
  local path="$1"
  if is_dry_run; then
    cat >/dev/null
    echo "  ~ write   $path"
    return 0
  fi
  echo "  ~ write   $path"
  mkdir -p "$(dirname "$path")"
  cat >"$path"
}

# Portable in-place sed (BSD/macOS and GNU).
sed_inplace() {
  local file="$1"
  shift
  sed -i.bak "$@" "$file" && rm -f "${file}.bak"
}
