# TextAnalyzer

A Spring Boot CLI application that scans a directory of text files, counts word frequencies across all files, and reports the top N most frequent words. Results can be printed to the console or saved as a JSON file.

## Features

- Recursively collects `.txt` files from a specified directory
- Filters words by minimum length
- Excludes stop words loaded from an external file
- Aggregates word counts across multiple files
- Supports **single-threaded** and **multithreaded** word counting with a configurable thread pool size
- Measures and reports total analysis execution time
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
  [--output <path-to-output.json>] \
  [--mode <SINGLE|MULTI>] \
  [--threads <number>]
```

### Options

| Option | Required | Description |
|--------|----------|-------------|
| `--dir` | ✅ | Path to the directory containing text files |
| `--min-length` | ✅ | Minimum word length (words shorter than this value are ignored) |
| `--top` | ✅ | Number of top words to display |
| `--stopwords` | ❌ | Path to a plain-text file with stop words (one or more words per line) |
| `--output` | ❌ | Path to a JSON file where results will be saved (if omitted, results are printed to stdout) |
| `--mode` | ❌ | Multithreading mode: `SINGLE` or `MULTI` (default: `MULTI`) |
| `--threads` | ❌ | Number of worker threads to use when `--mode MULTI` is active (default: `2`) |
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

**Run analysis using 8 threads in multithreaded mode:**
```bash
java -jar build/libs/TextAnalyzer-0.0.1-SNAPSHOT.jar \
  --dir ./texts \
  --min-length 4 \
  --top 10 \
  --mode MULTI \
  --threads 8
```

**Force single-threaded execution:**
```bash
java -jar build/libs/TextAnalyzer-0.0.1-SNAPSHOT.jar \
  --dir ./texts \
  --min-length 4 \
  --top 10 \
  --mode SINGLE
```

## Multithreading

TextAnalyzer can parse and count words across files either sequentially or in parallel, controlled by the [`MultithreadingMode`](src/main/java/org/example/textanalyzer/config/MultithreadingMode.java:3) enum (`SINGLE` or `MULTI`).

- **`SINGLE`** — files are parsed and aggregated one by one on the calling thread via [`WordCounter.countWords()`](src/main/java/org/example/textanalyzer/service/WordCounter.java:58).
- **`MULTI`** (default) — each file is parsed concurrently using a fixed-size thread pool created with `Executors.newFixedThreadPool(threads)` in [`WordCounter.countWordsMultithreading()`](src/main/java/org/example/textanalyzer/service/WordCounter.java:75). Each file produces a `Future<ParsingResult>`, and results are merged into a shared word-count map once all tasks complete.
- The pool size is configured via `--threads` (default: `2`). A parsing failure in one file is logged and does not stop the overall analysis.
- Total execution time (in milliseconds) is measured in [`TextAnalyzerRunner.call()`](src/main/java/org/example/textanalyzer/TextAnalyzerRunner.java:78) and included in the output under `analysisInfo.executionTimeMs`.
- The selected mode, thread count, number of processed files, and execution time are reported in both the console and JSON output.

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
    "directory": "texts",
    "minWordLength": 4,
    "topCount": 10,
    "mode": "MULTI",
    "threads": 8,
    "processedFiles": 12,
    "executionTimeMs": 143
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
│       ├── TextAnalyzerRunner.java        # CLI command (Picocli), exposes --mode/--threads options
│       ├── config/
│       │   ├── AppConfig.java
│       │   └── MultithreadingMode.java    # SINGLE / MULTI enum
│       ├── io/
│       │   └── FileCollector.java         # Collects .txt files from directory
│       ├── model/
│       │   ├── AnalysisInfo.java          # Analysis metadata (incl. mode, threads, execution time)
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
│           └── WordCounter.java           # Aggregates counts across files (single- or multithreaded)
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
