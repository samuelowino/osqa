#!/bin/bash

REPORT="target/site/jacoco/jacoco.xml"
README="README.md"
TMPFILE=".coverage.tmp"
LEAST_TESTED_TMP=".least_tested.tmp"

if [ ! -f "$REPORT" ]; then
  echo "❌ JaCoCo XML report not found at $REPORT"
  exit 1
fi

# Step 1: Generate new coverage report content
{
  echo "## 📊 Code Coverage Report"
  echo ""

  # Calculate overall coverage based on INSTRUCTION counter
  INSTR_COVERED=$(xmllint --xpath "string(//report/counter[@type='INSTRUCTION']/@covered)" "$REPORT")
  INSTR_MISSED=$(xmllint --xpath "string(//report/counter[@type='INSTRUCTION']/@missed)" "$REPORT")
  INSTR_TOTAL=$((INSTR_COVERED + INSTR_MISSED))

  if [ "$INSTR_TOTAL" -eq 0 ]; then
    OVERALL_PERCENT=0
  else
    OVERALL_PERCENT=$(awk "BEGIN {printf \"%.2f\", ($INSTR_COVERED / $INSTR_TOTAL) * 100}")
  fi

  if (( $(echo "$OVERALL_PERCENT < 50.0" | bc -l) )); then
    DISPLAY_OVERALL="${OVERALL_PERCENT}% ⚠️"
  else
    DISPLAY_OVERALL="${OVERALL_PERCENT}% ✅"
  fi

  echo "**Overall Coverage: $DISPLAY_OVERALL**"
  echo ""
  echo "| Metric      | Covered | Missed | Total | Coverage |"
  echo "|-------------|---------|--------|--------|----------|"

  METRICS=("INSTRUCTION" "LINE" "BRANCH" "METHOD" "CLASS" "COMPLEXITY")

  # Initialize an array to store least tested elements
  LEAST_TESTED=()

  for METRIC in "${METRICS[@]}"; do
    COVERED=$(xmllint --xpath "string(//report/counter[@type='$METRIC']/@covered)" "$REPORT")
    MISSED=$(xmllint --xpath "string(//report/counter[@type='$METRIC']/@missed)" "$REPORT")
    TOTAL=$((COVERED + MISSED))

    if [ "$TOTAL" -eq 0 ]; then
      PERCENT=0
    else
      PERCENT=$(awk "BEGIN {printf \"%.2f\", ($COVERED / $TOTAL) * 100}")
    fi

    if (( $(echo "$PERCENT < 50.0" | bc -l) )); then
      DISPLAY_PERCENT="${PERCENT}% ⚠️"
    else
      DISPLAY_PERCENT="${PERCENT}% ✅"
    fi

    echo "| $METRIC | $COVERED | $MISSED | $TOTAL | $DISPLAY_PERCENT |"

    # Check if the coverage is below 50% and add to least tested list
    if (( $(echo "$PERCENT < 50.0" | bc -l) )); then
      LEAST_TESTED+=("$METRIC: $PERCENT%")
    fi
  done

  # If there are least tested elements, list them
  if [ ${#LEAST_TESTED[@]} -gt 0 ]; then
    echo ""
    echo "### 🚨 Least Tested Elements (coverage below 50%)"
    for ELEMENT in "${LEAST_TESTED[@]}"; do
      echo "- $ELEMENT"
    done
  fi

} > "$TMPFILE"

# Step 2: Remove any old coverage section and replace with the new one
if grep -q "<!-- coverage start -->" "$README"; then
  sed -i.bak '/<!-- coverage start -->/,/<!-- coverage end -->/d' "$README"
fi

# Step 3: Append the new section at the end of the README
{
  echo ""
  echo "<!-- coverage start -->"
  cat "$TMPFILE"
  echo "<!-- coverage end -->"
} >> "$README"

rm "$TMPFILE"

echo "✅ README.md updated with full coverage report (including least tested elements)."