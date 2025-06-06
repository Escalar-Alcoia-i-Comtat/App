#!/bin/bash

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
DATE=$(date +'%Y-%m-%d')

curl -H 'Accept: application/json' -H 'Authorization: Token wlp_AsfTFX2nU6Wt0fuOYPEmmtiTCw18dsJ5dhYn' "https://hosted.weblate.org/api/projects/escalar-alcoia-i-comtat/credits/?start=2025-01-01&end=$DATE" > credits.json

mv credits.json "$SCRIPT_DIR/../composeApp/src/commonMain/composeResources/files/translations/credits.json"
