---
name: grouper-module-build
description: Use when building, versioning, installing, or diagnosing the Maven artifacts of esco-grouper-modules.
---

# Grouper Module Build

The active Maven reactor contains `esco-grouper-commons` and
`esco-grouper-sarapis-alimManager`. `ldap-source-adapter` is not an active
module and `bin/` is untracked build output.

## Build

Run from the repository root:

```sh
/opt/apache-maven-3.8.6/bin/mvn clean install
/opt/apache-maven-3.8.6/bin/mvn -pl esco-grouper-sarapis-alimManager -am clean install
```

The release artifacts are versioned `0.0.7-grouper-2.3.0`.

## Versioning

The parent POM owns the project version. When changing it, update the parent
references of both active modules and the explicit dependency from
`esco-grouper-sarapis-alimManager` to `esco-grouper-commons`.

Maven installs the built JARs and POMs in the configured local repository.
Do not edit `target/` or `bin/`; both are generated output.
