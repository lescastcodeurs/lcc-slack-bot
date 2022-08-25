All notable changes to this project's current version will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

### Added

- Ajout d'une nouvelle commande pour afficher la liste triée des catégories et de leurs libellés de manières plus
  condensée qu'auparavant (#39).
- Gestion des citations (#38). Les chevrons, qui sont HTML-encodés dans les messages Slack, sont désormais désencodés
  afin d'être correctement interprétés dans le markdown. Et les retours chariots sont désormais conservés sur le premier
  message.

### Changed

- Tous les mots clés associés aux commandes sont désormais affichés (#48).

### Fixed

### Deprecated

### Removed

### Dependencies

- Upgrade spotless from 6.8.0 to
  [6.10.0](https://github.com/diffplug/spotless/blob/main/plugin-gradle/CHANGES.md#6100---2022-08-23) (#34).
- Upgrade quarkus from 2.10.2 to [2.11.2](https://quarkus.io/blog/quarkus-2-11-2-final-released/) (#35, #47).
- Upgrade java version to [temurin-17.0.4+8](https://www.oracle.com/java/technologies/javase/17-0-4-relnotes.html)
  (#36).
- Upgrade gradle version from 7.4.2 to [7.5.1](https://docs.gradle.org/7.5/release-notes.html) (#36, #46).

### Internal
