---
name: java-21-standard
description: Modern Java 21 development standards including Virtual Threads and Pattern Matching.
---

# Java 21 Development Standards

Always prioritize Java 21 (LTS) features and modern best practices when generating or refactoring code.

## Core Language Features
- **Pattern Matching for switch**: Use `switch` expressions with pattern matching and `when` clauses for cleaner conditional logic.
- **Record Patterns**: Use deconstruction patterns for `record` types in `instanceof` and `switch`.
- **Sealed Classes**: Use `sealed` interfaces and classes to define restricted class hierarchies.
- **Records**: Prefer `record` for data-transfer objects (DTOs) and immutable data carriers.

## Concurrency (Project Loom)
- **Virtual Threads**: Prefer `Executors.newVirtualThreadPerTaskExecutor()` for high-throughput I/O-bound tasks instead of fixed thread pools.
- **Structured Concurrency**: Use `StructuredTaskScope` (if available via preview/library) to manage groups of related tasks.
- **Scoped Values**: Prefer Scoped Values over `ThreadLocal` for data sharing across virtual threads.

## Collections & APIs
- **Sequenced Collections**: Use `SequencedCollection`, `SequencedSet`, and `SequencedMap` methods (e.g., `addFirst`, `reversed()`) for ordered access.
- **String Templates**: Use `STR."..."` for string interpolation (ensure preview features are enabled if using strictly as per JDK 21 spec).
- **Emoji/Character Support**: Use updated `Character` methods for modern Unicode support.

## Code Quality
- **Text Blocks**: Always use multi-line text blocks (`"""`) for SQL, JSON, or HTML.
- **Var Keyword**: Use `var` for local variables where the type is clearly obvious from the initializer.
- **Immutability**: Default to immutable collections (`List.of()`, `Map.of()`) and `final` fields.

## Constraints
- AVOID legacy classes like `Vector`, `Hashtable`, or `Stack`.
- AVOID `ThreadLocal` when Virtual Threads are in use to prevent memory pinning.
