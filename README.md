# Révision — Gestion commerciale avec Hibernate & JPA

Ce projet regroupe les exercices de révision sur la gestion commerciale. J'ai utilisé Java 17, Hibernate et JPA avec une base H2 en mémoire pour lancer les exemples facilement.

Il contient le CRUD des catégories et produits, l'héritage entre clients et fournisseurs, et les commandes avec leurs lignes à clé composée. Les recherches utilisent JPQL et Criteria, avec des filtres et la pagination. Le rapport mensuel calcule les ventes par catégorie et les renvoie en JSON via un servlet.

## Lancer le projet

Avec Java 17 et Maven :

```bash
mvn clean test package
mvn exec:java
```

Pour garder le serveur ouvert :

```bash
mvn compile exec:java -Dexec.args=--serve
```

Le rapport est disponible sur [localhost:8087](http://127.0.0.1:8087/rapport-ventes?date=2026-10-11).

## Résultats

Les 7 tests passent. La démo affiche le CRUD, un rollback et le rapport de septembre 2026 : 249,97 pour l'informatique et 12,00 pour la papeterie. Les sorties d'exécution sont dans [evidence](evidence/).

Les captures de la démo et des tests viennent d’exécutions réelles dans le terminal.

![Résultats](screenshots/results.png)

![Tests](screenshots/tests.png)

![Rapport du servlet](screenshots/rapport-ventes.png)
