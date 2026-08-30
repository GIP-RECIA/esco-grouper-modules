---
name: grouper-dynamic-tree
description: Use when implementing or reviewing dynamic-tree-template, TREEGROUP, tree-char-separator, or Grouper folder and group creation.
---

# Grouper Dynamic Tree

A dynamic tree turns one evaluated attribute value into a chain of Grouper
folders and a terminal group. For example, with separator `/`, the value
`DSI/Support/N1` under `esco:Services` should produce folders
`esco:Services:DSI` and `esco:Services:DSI:Support`, then a terminal group
`esco:Services:DSI:Support:N1`.

Confirm this interpretation with the user before implementing it: the terminal
component may instead be intended as a folder or the complete value may be a
single group extension.

## Implementation contract

1. Split only the evaluated tree value, not unresolved template text.
2. Treat the configured separator as a literal, not a regular expression.
3. Reject empty path components such as `DSI//Support`, leading separators and
   trailing separators, unless the user explicitly defines another policy.
4. Reuse existing folders and create missing folders in order.
5. Create or retrieve the terminal group using the existing privilege and
   membership handling.
6. Do not classify a tree definition as a normal group merely because it is not
   a `FOLDER`.
7. Preserve `currentPath` for following XML siblings.

## Required scenarios

Validate at least these cases:

- One component: `N1`.
- Several components: `DSI/Support/N1`.
- A second person in the same branch.
- A sibling definition after the dynamic tree in the XML.
- Existing intermediate folders.
- A non-default literal separator when supported.

Check privileges and memberships on the terminal group, and folder privileges
on each created intermediate folder when the configuration requires them.

## Aggregation groups

When `aggregate-group-prefix` is configured, create an aggregate group in
each intermediate folder. Nest the child aggregate group into its parent
aggregate group, then nest the terminal group into the deepest aggregate.
For `DSI/Support/N1` and prefix `Tous_`, `N1` is a member of
`Tous_Support`, which is a member of `Tous_DSI`.

The tree extension, display extension and separator are evaluated templates.
They must use the project's `%NAME%` placeholder syntax and the extension and
display extension must resolve to the same number of components.
