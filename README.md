# Holiday Home Manager

Backend Spring Boot (Java 21, Maven, PostgreSQL) per la gestione di una casa vacanze:
una palazzina di 16 appartamenti, con anagrafica ospiti, check-in/check-out e rilascio
di chiavi digitali per il portone principale e per la porta di ciascun appartamento.

Progetto realizzato con dominio con almeno 8 tabelle e una gerarchia di ereditarietà,
gestione utenti completa, API REST con gestione errori consistente, query realistiche
(filtri, ordinamenti, aggregazioni), integrazione con due API di terze parti,
collection Postman e questo README.

## Panoramica del dominio

- Solo lo **staff** (ruoli `ADMIN` e `RECEPTIONIST`) ha un account e fa login: gli
  ospiti sono anagrafiche gestite dallo staff, non utenti dell'applicazione.
- Ogni appartamento ha una propria porta digitale (`ApartmentDoor`); esiste inoltre
  un portone d'ingresso condiviso (`MainEntranceDoor`). Entrambi ereditano da `Door`
  (strategia `JOINED`): è la gerarchia di ereditarietà richiesta dal dominio.
- Una prenotazione (`Booking`) collega un appartamento, uno o più ospiti (`Guest`,
  tramite `BookingGuest`) ed eventuali servizi extra (`ExtraService`, tramite
  `BookingExtraService`).
- Al **check-in** vengono generate due chiavi digitali (`DigitalKey`) per la
  prenotazione — una per il portone, una per l'appartamento — con validità limitata
  al soggiorno, e viene inviata un'email all'ospite principale con i relativi codici.
  Al **check-out** le chiavi vengono revocate.
- Al primo avvio un seeder (`DataSeeder`) crea automaticamente il portone principale
  e i 16 appartamenti (con relativa porta) se il database è vuoto.

## Modello dati (12 tabelle)

`users`, `apartments`, `apartment_photos`, `guests`, `bookings`, `booking_guests`,
`doors` (+ `main_entrance_doors`, `apartment_doors` per la gerarchia), `digital_keys`,
`extra_services`, `booking_extra_services`.

## API di terze parti

1. **Cloudinary** — upload delle foto degli appartamenti e dell'avatar dello staff.
2. **Brevo** (`https://api.brevo.com/v3/smtp/email`) — invio dell'email transazionale
   con i codici delle chiavi digitali al check-in. Il `messageId` restituito dall'API
   viene salvato su `DigitalKey.notificationReference`, a dimostrazione dell'uso
   concreto della risposta dell'API esterna nella logica applicativa. Se la chiamata
   fallisce (chiave non configurata, servizio non raggiungibile) il check-in **non**
   viene bloccato: l'errore è loggato e il riferimento resta vuoto.

## Avvio del progetto

### Prerequisiti

- Java 21
- Maven (oppure l'IDE, es. IntelliJ, che lo gestisce automaticamente)
- PostgreSQL in esecuzione localmente
- Un account Cloudinary (gratuito) e un account Brevo (gratuito) per le due
  integrazioni esterne

### Configurazione

1. Crea il database PostgreSQL indicato in `PG_DB_NAME` (default `holiday_home_db`).
2. Compilare l' `env.properties` (stessa cartella di `pom.xml`)
   e valorizza tutti i campi.
3. Avvia l'applicazione:
   - da IDE: esegui `HolidayHomeManagerApplication`
   - da terminale: `mvn spring-boot:run`

L'app parte sulla porta indicata da `PORT` (default `3001`). Hibernate crea/aggiorna
lo schema automaticamente (`ddl-auto=update`) e il seeder popola portone e appartamenti
al primo avvio.

### Variabili d'ambiente (`env.properties`)

| Variabile | Descrizione |
|---|---|
| `PORT` | Porta HTTP (default 3001) |
| `PG_DB_NAME`, `PG_USERNAME`, `PG_PASSWORD` | Connessione PostgreSQL |
| `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_SECRET` | Credenziali Cloudinary |
| `JWT_SECRET` | Stringa segreta per firmare i token JWT |
| `BREVO_API_KEY` | API key Brevo |
| `BREVO_SENDER_EMAIL`, `BREVO_SENDER_NAME` | Mittente delle email inviate |
| `MAIN_ENTRANCE_LABEL` | Etichetta del portone principale (opzionale) |
| `TOTAL_APARTMENTS` | Numero di appartamenti da generare al primo avvio (default 16) |

## Autenticazione

Tutti gli endpoint richiedono un JWT tranne `/auth/**`. Il flusso:

1. `POST /auth/register` per creare il primo utente staff (ADMIN).
2. `POST /auth/login` per ottenere il token.
3. Header `Authorization: Bearer <token>` su ogni richiesta successiva.

Gli endpoint di sola amministrazione richiedono il ruolo `ADMIN` (es. creare/modificare
appartamenti, cancellare utenti); altri sono accessibili anche a `RECEPTIONIST`
(es. check-in/check-out, gestione prenotazioni e ospiti).

## Principali funzionalità / endpoint

- **Auth**: `POST /auth/register`, `POST /auth/login`
- **Staff**: `GET /users`, `GET/PUT /users/me`, `PATCH /users/me/avatar`,
  `PUT/DELETE /users/{id}`
- **Appartamenti**: CRUD (`/apartments`), `GET /apartments/available?checkInDate=&checkOutDate=&minGuests=`,
  `PATCH /apartments/{id}/status`, `PATCH /apartments/{id}/photos` (upload Cloudinary)
- **Ospiti**: CRUD (`/guests`), `GET /guests/search?lastName=`
- **Prenotazioni**: CRUD (`/bookings`), `GET /bookings/status/{status}`,
  `GET /bookings/today/arrivals`, `GET /bookings/today/departures`,
  `GET /bookings/stats/monthly-revenue`, `POST /bookings/{id}/check-in`,
  `POST /bookings/{id}/check-out`, `POST /bookings/{id}/cancel`,
  `POST /bookings/{id}/extra-services`
- **Chiavi digitali**: `GET /digital-keys/booking/{bookingId}`
- **Servizi extra**: CRUD (`/extra-services`)

Tutte le richieste ed esempi di payload sono nella collection Postman allegata
(`postman/holiday-home-manager.postman_collection.json`).

## Note di progettazione / limiti noti

- La registrazione staff (`POST /auth/register`) è volutamente aperta per semplicità
  (permette di creare il primo ADMIN); in un contesto reale andrebbe riservata a un
  ADMIN già autenticato o protetta da un flusso di setup iniziale.

