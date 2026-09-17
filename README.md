# TextAnalyzer

A Spring Boot CLI application that scans a directory of text files, counts word frequencies across all files, and reports the top N most frequent words. Results can be printed to the console or saved as a JSON file.

## Features

- Recursively collects `.txt` files from a specified directory
- Filters words by minimum length
- Excludes stop words loaded from an external file
- Aggregates word counts across multiple files
- Outputs results to the **console** or a **JSON file**
- Reports parsing errors (empty files, unreadable files) without stopping execution

## Requirements

| Tool | Version |
|------|---------|
| Java | 17+     |
| Gradle | 8+    |

## Tech Stack

- **Spring Boot** 4.1.0
- **Picocli** 4.7.7 (CLI parsing)
- **Jackson** (JSON serialization)
- **JUnit 5** (testing)

## Building

```bash
./gradlew build
```

The executable JAR will be placed in `build/libs/`.

## Usage

```bash
java -jar build/libs/TextAnalyzer-0.0.1-SNAPSHOT.jar \
  --dir <path-to-directory> \
  --min-length <number> \
  --top <number> \
  [--stopwords <path-to-stopwords-file>] \
  [--output <path-to-output.json>]
```

### Options

| Option | Required | Description |
|--------|----------|-------------|
| `--dir` | ✅ | Path to the directory containing text files |
| `--min-length` | ✅ | Minimum word length (words shorter than this value are ignored) |
| `--top` | ✅ | Number of top words to display |
| `--stopwords` | ❌ | Path to a plain-text file with stop words (one or more words per line) |
| `--output` | ❌ | Path to a JSON file where results will be saved (if omitted, results are printed to stdout) |
| `--help` | ❌ | Display help message |

### Examples

**Print top 10 words (minimum 4 characters) to console:**
```bash
java -jar build/libs/TextAnalyzer-0.0.1-SNAPSHOT.jar \
  --dir ./texts \
  --min-length 4 \
  --top 10
```

**Save top 5 words to a JSON file, excluding stop words:**
```bash
java -jar build/libs/TextAnalyzer-0.0.1-SNAPSHOT.jar \
  --dir ./texts \
  --min-length 3 \
  --top 5 \
  --stopwords ./stopwords.txt \
  --output ./result.json
```

## Output Formats

### Console

```
1. example — 42
2. analyzer — 35
3. words — 28
```

### JSON (`--output` specified)

```json
{
  "analysisInfo": {
    "directoryName": "texts",
    "minWordLength": 4,
    "topWords": 10
  },
  "words": [
    { "word": "example", "count": 42 },
    { "word": "analyzer", "count": 35 }
  ],
  "errors": [
    { "fileName": "empty.txt", "message": "File is empty." }
  ]
}
```

## Stop Words File Format

A plain text file with words separated by whitespace or newlines. All words are lowercased automatically.

```
the
and is are
a an
```

## Project Structure

```
src/
└── main/
│   └── java/org/example/textanalyzer/
│       ├── TextAnalyzerApplication.java   # Spring Boot entry point
│       ├── TextAnalyzerRunner.java        # CLI command (Picocli)
│       ├── config/
│       │   └── AppConfig.java
│       ├── io/
│       │   └── FileCollector.java         # Collects .txt files from directory
│       ├── model/
│       │   ├── AnalysisInfo.java          # Analysis metadata
│       │   ├── WordCount.java             # Word + count pair
│       │   ├── WordCountResult.java       # Internal word counting result
│       │   └── CountTopWordsResult.java   # Final top-N result
│       ├── output/
│       │   ├── Printer.java               # Output interface
│       │   ├── UnifiedPrinter.java        # Routes to console or JSON
│       │   ├── ConsolePrinter.java        # Prints to stdout
│       │   ├── JsonPrinter.java           # Writes JSON file
│       │   └── ResultOutput.java          # Output record
│       ├── parser/
│       │   ├── FileParser.java            # Reads and tokenizes files
│       │   ├── ParsingOptions.java        # Min length + stop words config
│       │   ├── ParsingResult.java         # Words + error from one file
│       │   └── ParsingError.java          # File-level error record
│       └── service/
│           └── WordCounter.java           # Aggregates counts across files
└── test/
    └── java/org/example/textanalyzer/    # Unit and integration tests
```

## Running Tests

```bash
./gradlew test
```

## Exit Codes

| Code | Meaning |
|------|---------|
| `0` | Success |
| `1` | Invalid arguments or runtime error |
