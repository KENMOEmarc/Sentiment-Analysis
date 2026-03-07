# sa-backend

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.10-brightgreen)
![Maven](https://img.shields.io/badge/Maven-3.8+-red)
![MariaDB](https://img.shields.io/badge/MariaDB-Database-blue)
![Google Gemini](https://img.shields.io/badge/Google%20Gemini-AI-yellow)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-green)
![Lombok](https://img.shields.io/badge/Lombok-Available-green)
![Spring AI](https://img.shields.io/badge/Spring%20AI-1.1.0-blue)

**API d'analyse de sentiments d'avis utilisateurs avec Google Gemini**

Cette application Spring Boot permet de recueillir des avis utilisateurs, d'analyser leur sentiment (positif, négatif, neutre) grâce à l'IA générative de Google (Gemini) et de générer automatiquement un email personnalisé en fonction du commentaire, qui est ensuite envoyé à l'utilisateur.

## Fonctionnalités

- Exposition d'une API REST pour soumettre un avis (nom, email, commentaire)
- Analyse du sentiment du commentaire via Gemini (positif, négatif, neutre)
- Génération d'un email contextuel adapté au sentiment (remerciement, excuses, etc.) via Gemini
- Envoi de l'email à l'utilisateur (via SMTP)
- Stockage des avis en base de données MariaDB
- Documentation interactive de l'API avec Swagger UI

## Technologies utilisées

- Java 17
- Spring Boot 3.5.10
- Spring Data JPA
- Spring Web
- Spring Mail
- Spring AI 1.1.0 (starter Google GenAI)
- MariaDB
- Lombok
- Springdoc OpenAPI 2.0.4
- Maven

## Prérequis

- JDK 17 ou supérieur
- Maven 3.8+
- MariaDB installée et accessible
- Compte Google AI Studio avec une clé API pour les modèles Gemini
- (Optionnel) Un serveur SMTP pour l'envoi d'emails

## Configuration

Créez un fichier `application.properties` dans `src/main/resources/` avec les paramètres suivants :

```properties
# Base de données MariaDB
spring.datasource.url=jdbc:mariadb://localhost:3306/sa_backend
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MariaDBDialect

# Google AI (Gemini)
spring.ai.google.genai.api-key=VOTRE_CLE_API_GOOGLE

# Mail SMTP (exemple avec Gmail)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=votre.email@gmail.com
spring.mail.password=votre-mot-de-passe
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Swagger UI
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

## Installation et exécution

1. **Cloner le dépôt** (ou télécharger les sources)
2. **Compiler le projet** :
   ```bash
   mvn clean install
   ```
3. **Lancer l'application** :
   ```bash
   mvn spring-boot:run
   ```
   L'application démarre sur le port `8080` par défaut.

## Endpoints de l'API

### Clients (`/clients`)

| Méthode | URL            | Description                          | Corps (JSON)                                 | Réponse                     |
|---------|----------------|--------------------------------------|----------------------------------------------|-----------------------------|
| POST    | `/clients`     | Créer un nouveau client              | `{ "nom": "...", "email": "..." }`           | `201 Created`               |
| GET     | `/clients`     | Récupérer tous les clients           | -                                            | Liste de clients            |
| GET     | `/clients/{id}`| Récupérer un client par son ID       | -                                            | Client                      |
| PUT     | `/clients/{id}`| Remplacer complètement un client     | `{ "nom": "...", "email": "..." }`           | `204 No Content`            |
| PATCH   | `/clients/{id}`| Mise à jour partielle d'un client    | `{ "nom": "...", "email": "..." }` (partiel) | Client mis à jour           |
| DELETE  | `/clients/{id}`| Supprimer un client                  | -                                            | `200 OK`                    |

> **Note** : La méthode PATCH accepte un objet JSON partiel et fusionne les modifications avec les données existantes. Le champ `id` n'est pas autorisé dans le corps de la requête PATCH.

### Sentiments (`/sentiments`)

| Méthode | URL                 | Description                          | Corps (JSON)                                 | Réponse                     |
|---------|---------------------|--------------------------------------|----------------------------------------------|-----------------------------|
| POST    | `/sentiments`       | Ajouter un nouveau sentiment (avis)  | `{ "commentaire": "...", "client": {...} }`  | `201 Created`               |
| GET     | `/sentiments`       | Récupérer tous les sentiments        | -                                            | Liste de sentiments         |
| GET     | `/sentiments/{id}`  | Récupérer un sentiment par son ID    | -                                            | Sentiment                   |
| DELETE  | `/sentiments/{id}`  | Supprimer un sentiment               | -                                            | `200 OK`                    |

**Traitement spécifique lors de la création d'un sentiment** :  
Lors de l'appel `POST /sentiments`, le service `SentimentServiceImpl` :
1. Analyse le commentaire avec Google Gemini pour déterminer le sentiment (POSITIF, NÉGATIF, NEUTRE).
2. Génère un email personnalisé adapté au sentiment.
3. Envoie l'email à l'adresse du client associé.
4. Sauvegarde le sentiment avec le résultat de l'analyse.

### Documentation Swagger

Une fois l'application lancée, la documentation interactive est disponible à :  
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Structure du projet

```
sa-backend/
├── src/
│   ├── main/
│   │   ├── java/ken/tar/sa-backend/
│   │   │   ├── controller/    
│   │   │   ├── entity/             
│   │   │   ├── repository/       
│   │   │   ├── service/           
│   │   │   └── SaBackendApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── ...
│   └── test/                     
├── pom.xml
└── README.md
```

## Contribution

Les contributions sont les bienvenues. Veuillez ouvrir une *issue* pour discuter des changements importants avant de soumettre une *pull request*.
