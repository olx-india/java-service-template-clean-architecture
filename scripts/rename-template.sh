#!/usr/bin/env bash
# Rename this template for a new service.
# Usage:
#   ./scripts/rename-template.sh com.acme.orders orders-service OrdersApplication
set -euo pipefail

if [[ $# -lt 3 ]]; then
  echo "Usage: $0 <new.base.package> <artifactId> <ApplicationClassName>"
  echo "Example: $0 com.acme.orders orders-service OrdersApplication"
  exit 1
fi

NEW_PACKAGE="$1"
ARTIFACT_ID="$2"
APP_CLASS="$3"
OLD_PACKAGE="com.olx.boilerplate"
OLD_ARTIFACT="boilerplate"
OLD_APP="BoilerplateApplication"

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

NEW_PACKAGE_PATH="${NEW_PACKAGE//.//}"
OLD_PACKAGE_PATH="${OLD_PACKAGE//.//}"

echo "Renaming package ${OLD_PACKAGE} -> ${NEW_PACKAGE}"
echo "Artifact ${OLD_ARTIFACT} -> ${ARTIFACT_ID}"
echo "Main class ${OLD_APP} -> ${APP_CLASS}"

# Update text references
find . -type f \
  \( -name "*.java" -o -name "*.xml" -o -name "*.yml" -o -name "*.yaml" -o -name "*.md" -o -name "*.properties" -o -name "Makefile" \) \
  ! -path "./.git/*" ! -path "./target/*" \
  -print0 | while IFS= read -r -d '' file; do
  if grep -q "${OLD_PACKAGE}\|${OLD_ARTIFACT}\|${OLD_APP}" "$file" 2>/dev/null; then
    sed -i.bak \
      -e "s/${OLD_PACKAGE}/${NEW_PACKAGE}/g" \
      -e "s/${OLD_ARTIFACT}/${ARTIFACT_ID}/g" \
      -e "s/${OLD_APP}/${APP_CLASS}/g" \
      "$file"
    rm -f "${file}.bak"
  fi
done

# Move Java sources
for SRC in src/main/java src/test/java; do
  if [[ -d "${SRC}/${OLD_PACKAGE_PATH}" ]]; then
    mkdir -p "${SRC}/$(dirname "${NEW_PACKAGE_PATH}")"
    mv "${SRC}/${OLD_PACKAGE_PATH}" "${SRC}/${NEW_PACKAGE_PATH}"
    # clean empty parents
    find "${SRC}/com" -type d -empty -delete 2>/dev/null || true
  fi
done

# Rename main application file if present
APP_DIR="src/main/java/${NEW_PACKAGE_PATH}"
if [[ -f "${APP_DIR}/${APP_CLASS}.java" ]]; then
  :
elif [[ -f "${APP_DIR}/BoilerplateApplication.java" ]]; then
  mv "${APP_DIR}/BoilerplateApplication.java" "${APP_DIR}/${APP_CLASS}.java"
fi

echo "Done. Review git diff, update README clone URLs, and run: make test"
