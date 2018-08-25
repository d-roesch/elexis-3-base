# Import
## Patient
- Example for creating new Patient: **_ch.elexis.core.ui.contacts.dialogs.PatientErfassenDialog #129_**

## XID Query
1) Query all Kontakt with istOrganisation flag
2) Perform on each Kontakt object following call: **_public static IPersistentObject findObject(final String domain, final String id){_**

Maybe this works:

```java
Query<Kontakt> query = new Query<>(Kontakt.class);
query.add(Kontakt.FLD_IS_ORGANIZATION, Query.EQUALS, 1);
List<Kontakt> contacts = query.execute();

List<Kontakt> result = contacts.stream.filter(contact -> contact.findObject("EAN", GLN).collect(Collectors.toList());

```

## Konsulatation
### Leistung hinzufügen:
Konsultationen#addLeistung

## Tarmed

- Was ist eine Seite?
- Für was braucht es den Parent?

### Tarmedleistung hinzufügen
TarmedLeistung.load(ID) -> Was für eine ID?