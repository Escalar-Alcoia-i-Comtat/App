#!/usr/bin/env bash
set -euo pipefail

# List of files to process
files=(
  "./composeApp/src/commonMain/composeResources/values-ca/strings.xml"
  "./composeApp/src/commonMain/composeResources/values-es/strings.xml"
)

for f in "${files[@]}"; do
  if [[ -f "$f" ]]; then
    # Use double quotes so we can embed the single-quote replacement directly
    #   s/\\'/\'/g   → match backslash+single-quote, replace with single-quote
    sed -i.bak "s/\\\\'/\'/g" "$f"
    echo "Processed: $f (backup saved as $f.bak)"
  else
    echo "Warning: File not found: $f" >&2
  fi
done
