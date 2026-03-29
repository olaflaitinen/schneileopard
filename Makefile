.PHONY: help build compile test clean format docs examples bench publish release

help:
	@echo "Schnéileopard Build Targets"
	@echo "============================"
	@echo ""
	@echo "Development:"
	@echo "  make build       - Clean build all modules"
	@echo "  make compile     - Compile all modules"
	@echo "  make test        - Run all tests"
	@echo "  make format      - Format all code"
	@echo "  make clean       - Clean build artifacts"
	@echo ""
	@echo "Documentation:"
	@echo "  make docs        - Generate Scaladoc"
	@echo ""
	@echo "Running:"
	@echo "  make examples    - Run example application"
	@echo "  make bench       - Build and info for benchmarks"
	@echo "  make console     - Start Scala REPL"
	@echo ""
	@echo "Publishing:"
	@echo "  make publish     - Publish artifacts to local repository"
	@echo "  make release     - Create release (requires version tag)"
	@echo ""
	@echo "Project Info:"
	@echo "  make info        - Show project information"
	@echo ""

build: clean compile test
	@echo "Build complete"

compile:
	sbt clean compile

test:
	sbt test

clean:
	sbt clean
	rm -rf target/
	rm -rf modules/*/target/

format:
	sbt scalafmtAll

docs:
	sbt doc
	@echo "Documentation generated in target/scala-3.6.1/api/index.html"

examples:
	sbt "examples/run"

bench:
	sbt "bench/assembly"
	@echo "Benchmark JAR built to modules/bench/target/scala-3.6.1/schneileopard-bench-0.1.0-jmh.jar"
	@echo "Run with: java -jar modules/bench/target/scala-3.6.1/schneileopard-bench-0.1.0-jmh.jar"

console:
	sbt console

publish:
	sbt publishLocal

release:
	@echo "Release requires a git tag: git tag -a v0.2.0 -m \"Release 0.2.0\""
	@echo "Push tag with: git push origin v0.2.0"
	@echo "GitHub Actions will automatically build and publish"

check-format:
	sbt scalafmtCheckAll

check-deps:
	sbt dependencyTree

check-all: check-format test docs
	@echo "All checks passed"

info:
	@echo "Schnéileopard Project Information"
	@echo "=================================="
	@echo ""
	@echo "Repository: https://github.com/olaflaitinen/schneileopard"
	@echo "License: EUPL 1.2"
	@echo ""
	sbt scalaVersion
	@echo ""
	@echo "Modules:"
	@echo "  - schneileopard-core"
	@echo "  - schneileopard-omics"
	@echo "  - schneileopard-graph"
	@echo "  - schneileopard-ai"
	@echo "  - schneileopard-io"
	@echo "  - schneileopard-examples"
	@echo "  - schneileopard-bench"
	@echo ""

# Default target
.DEFAULT_GOAL := help
