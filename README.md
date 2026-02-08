# MiniGoogleSearch

A minimal search engine implementation in Java.

## How to Run

The project is compiled in the `bin` directory. You can run it using the following commands:

### 1. Build the Index
If you need to re-index the `data` directory (optional if `index.ser` already exists):
```bash
java -cp bin cli.Main --index
```

### 2. Search
To search for terms:
```bash
java -cp bin cli.Main --query <your query> --top k <number of results>
```

**Example:**
```bash
java -cp bin cli.Main --query "search algorithm" --top k 5
```
