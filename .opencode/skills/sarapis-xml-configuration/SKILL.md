---
name: sarapis-xml-configuration
description: Use when editing or diagnosing sarapis-groups.dtd, sarapis-groups-service XML files, SGSParsingUtil, or SGSAttributeHandler.
---

# Sarapis XML Configuration

The Sarapis group service reads an XML definition through a SAX parser and a
local DTD. Treat the DTD, the parser and the example configuration as one
contract.

## Required checks

1. Identify the XML tag and attributes involved.
2. Confirm that the DTD permits the tag in its parent and declares every
   required and optional attribute.
3. Confirm that `SGSParsingUtil` recognizes both start and end events without
   corrupting `currentPath` or recursive privileges.
4. Confirm that `SGSAttributeHandler.reset()` resets every added attribute and
   that optional defaults are intentional.
5. Check the XML is well formed and validate it against the DTD before testing
   Grouper behavior.

## Path invariant

Only an element that pushes `currentPath` may pop it in `endElement`. If a new
tag is a group-like leaf, it must not modify the enclosing folder path. If it
is a container, its start and end handling must be symmetrical.

## Template invariant

`ordered-template-elements` fixes the positional order of values passed to
`evaluateTemplate`. A new template key must be declared there and used with
the exact same spelling in `extension`, `display-extension`, `description`,
and `distribution-by`.

## Validation commands

Prefer a read-only XML/DTD validation command if available. Then run the
smallest Maven module build that compiles
`esco-grouper-sarapis-alimManager`. Report environmental dependency failures
separately from XML or Java failures.
