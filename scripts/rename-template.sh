#!/usr/bin/env bash
# Rename this template for a new service: base package, Maven artifactId and
# main application class.
#
# Usage:
#   ./scripts/rename-template.sh <new.base.package> <artifactId> <ApplicationClassName>
#   ./scripts/rename-template.sh com.acme.orders orders-service OrdersApplication
#
# Environment:
#   DRY_RUN=1  print files that would change / move; touch nothing
#   FORCE=1    allow a dirty git working tree
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"
# shellcheck source=lib/common.sh
source "$ROOT/scripts/lib/common.sh"

if [[ $# -lt 3 ]]; then
  echo "Usage: $0 <new.base.package> <artifactId> <ApplicationClassName>"
  echo "Example: $0 com.acme.orders orders-service OrdersApplication"
  exit 1
fi

NEW_PACKAGE="$1"
ARTIFACT_ID="$2"
APP_CLASS="$3"

[[ "$NEW_PACKAGE" =~ ^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)+$ ]] \
  || die "PACKAGE '${NEW_PACKAGE}' must be a lowercase dotted Java package, e.g. com.acme.orders"
[[ "$ARTIFACT_ID" =~ ^[a-z][a-z0-9.-]*$ ]] \
  || die "ARTIFACT '${ARTIFACT_ID}' must be lowercase letters, digits, '.' or '-', e.g. orders-service"
[[ "$APP_CLASS" =~ ^[A-Z][A-Za-z0-9]*$ ]] \
  || die "APP '${APP_CLASS}' must be a PascalCase Java class name, e.g. OrdersApplication"

[[ -d "src/main/java/${OLD_PACKAGE_PATH}" ]] \
  || die "Template package ${OLD_PACKAGE} not found under src/main/java — already renamed?"

require_clean_git_tree

NEW_PACKAGE_PATH="${NEW_PACKAGE//.//}"

echo "==> Renaming template"
echo "    package  ${OLD_PACKAGE} -> ${NEW_PACKAGE}"
echo "    artifact ${OLD_ARTIFACT} -> ${ARTIFACT_ID}"
echo "    main     ${OLD_APP} -> ${APP_CLASS}"
is_dry_run && echo "    DRY_RUN=1 — no files will be changed"

# Update text references (scripts/ is excluded: it holds the old names on purpose).
echo "==> Updating references"
find . -type f \
  \( -name "*.java" -o -name "*.xml" -o -name "*.yml" -o -name "*.yaml" -o -name "*.md" -o -name "*.mdc" \
     -o -name "*.properties" -o -name "Makefile" -o -name "Dockerfile*" \) \
  ! -path "./.git/*" ! -path "./target/*" ! -path "./scripts/*" ! -path "./.idea/*" \
  -print0 | while IFS= read -r -d '' file; do
  if grep -q -e "${OLD_PACKAGE}" -e "${OLD_ARTIFACT}" -e "${OLD_APP}" "$file" 2>/dev/null; then
    echo "  ~ edit    ${file#./}"
    is_dry_run && continue
    if [[ "$file" == *.java ]]; then
      # Java: package and class names only. The bare artifact name would corrupt
      # identifiers such as boilerplateOpenApi (artifactIds may contain '-').
      sed_inplace "$file" \
        -e "s/${OLD_PACKAGE}/${NEW_PACKAGE}/g" \
        -e "s/${OLD_APP}/${APP_CLASS}/g" \
        -e "s/\"${OLD_ARTIFACT}\"/\"${ARTIFACT_ID}\"/g"
    else
      sed_inplace "$file" \
        -e "s/${OLD_PACKAGE}/${NEW_PACKAGE}/g" \
        -e "s/${OLD_APP}/${APP_CLASS}/g" \
        -e "s/${OLD_ARTIFACT}/${ARTIFACT_ID}/g"
    fi
  fi
done

# Use the artifactId as the logical service name (logback "service" field).
for f in src/main/resources/application.yaml src/main/resources/application-*.yaml; do
  [[ -f "$f" ]] || continue
  grep -qE '^service:' "$f" || continue
  echo "  ~ edit    ${f} (service.name -> ${ARTIFACT_ID})"
  is_dry_run && continue
  perl -0pi -e 's/^(service:\n  name: )"?[^"\n]*"?$/$1"'"${ARTIFACT_ID}"'"/m' "$f"
done

# Move Java sources
echo "==> Moving sources"
for SRC in src/main/java src/test/java; do
  if [[ -d "${SRC}/${OLD_PACKAGE_PATH}" ]]; then
    echo "  > move    ${SRC}/${OLD_PACKAGE_PATH} -> ${SRC}/${NEW_PACKAGE_PATH}"
    is_dry_run && continue
    TMP_DIR="${SRC}/.rename-tmp-$$"
    mv "${SRC}/${OLD_PACKAGE_PATH}" "$TMP_DIR"
    find "${SRC}" -type d -empty -delete 2>/dev/null || true
    mkdir -p "${SRC}/$(dirname "${NEW_PACKAGE_PATH}")"
    mv "$TMP_DIR" "${SRC}/${NEW_PACKAGE_PATH}"
  fi
done

# Rename the main application class and its test
MAIN_DIR="src/main/java/${NEW_PACKAGE_PATH}"
TEST_DIR="src/test/java/${NEW_PACKAGE_PATH}"
if [[ "${OLD_APP}" != "${APP_CLASS}" ]]; then
  for pair in "${MAIN_DIR}/${OLD_APP}.java:${MAIN_DIR}/${APP_CLASS}.java" \
              "${TEST_DIR}/${OLD_APP}Tests.java:${TEST_DIR}/${APP_CLASS}Tests.java"; do
    from="${pair%%:*}"
    to="${pair##*:}"
    if is_dry_run; then
      echo "  > move    ${from/${NEW_PACKAGE_PATH}/${OLD_PACKAGE_PATH}} -> ${to}"
    elif [[ -f "$from" ]]; then
      echo "  > move    ${from} -> ${to}"
      mv "$from" "$to"
    fi
  done
fi

echo
if is_dry_run; then
  echo "Dry run complete. Re-run without DRY_RUN=1 to apply."
else
  echo "Done. Review 'git status' / 'git diff', update README clone URLs, and run: make test"
fi
