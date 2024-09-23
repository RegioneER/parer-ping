
## 6.2.1 (23-09-2024)

### Bugfix: 2
- [#34053](https://parermine.regione.emilia-romagna.it/issues/34053) Correzione dell'errore alla creazione di versatore
- [#34047](https://parermine.regione.emilia-romagna.it/issues/34047) Correzione degli errori sulla duplicazione e importazione di versatori

## 6.2.0 (18-09-2024)

### Bugfix: 6
- [#33953](https://parermine.regione.emilia-romagna.it/issues/33953) Correzione dell'errore in cancellazione cartella temporanee in inserimento trasformazione
- [#33870](https://parermine.regione.emilia-romagna.it/issues/33870) Correzione del comportamento di oggetto verificato con sessioni precedenti non verificate
- [#33868](https://parermine.regione.emilia-romagna.it/issues/33868) correzione dell'errore critico quando si modifica un progetto sisma
- [#33865](https://parermine.regione.emilia-romagna.it/issues/33865) Correzione descrizione versatore mostrato nell'intestazione a sinistra all'apertura di organizzazione da Siam
- [#31002](https://parermine.regione.emilia-romagna.it/issues/31002) Correzione della mancata codifica in caso di UnmarshalException
- [#27890](https://parermine.regione.emilia-romagna.it/issues/27890) Correzione errore di inserimento parametro trasformazione con valore null

### Novità: 8
- [#33956](https://parermine.regione.emilia-romagna.it/issues/33956) Correzione del problema di progetto in ERRORE con oggetto in versamento senza errori
- [#33866](https://parermine.regione.emilia-romagna.it/issues/33866) Modifica caricamento lista Ambiente Sacer
- [#33815](https://parermine.regione.emilia-romagna.it/issues/33815) Modifica al dettaglio versatore per mostrare il path della cartella DA_VERSARE
- [#33155](https://parermine.regione.emilia-romagna.it/issues/33155) Rimuovere il limite hardcodato in versamento oggetto per object storage
- [#33041](https://parermine.regione.emilia-romagna.it/issues/33041) Creazione del pulsante per la creazione della cartella DA_VERSARE su path FTP del versatore
- [#32650](https://parermine.regione.emilia-romagna.it/issues/32650) Associazione tra parametri e versione dell'applicazione
- [#32647](https://parermine.regione.emilia-romagna.it/issues/32647) Realizzazione della funzione di versamento oggetto da path puntuale su FTP
- [#30347](https://parermine.regione.emilia-romagna.it/issues/30347) Pulizia mappaggi jpa

## 6.1.0 (19-08-2024)

### Novità: 1
- [#33258](https://parermine.regione.emilia-romagna.it/issues/33258) Aggiornamento librerie obsolete 2024

## 6.0.0 (13-08-2024)

### Novità: 2
- [#32825](https://parermine.regione.emilia-romagna.it/issues/32825) Modifiche alla conservazione dei log dei job
- [#30801](https://parermine.regione.emilia-romagna.it/issues/30801) Aggiornamento a Java 11

## 5.3.0 (16-07-2024)

### Bugfix: 2
- [#30193](https://parermine.regione.emilia-romagna.it/issues/30193) Correzione errore pagina grigia dopo il download nella pagina Dettaglio versione XSD del Tipo Oggetto
- [#27986](https://parermine.regione.emilia-romagna.it/issues/27986) Correzione job Recupero da Sacer

### Novità: 9
- [#32724](https://parermine.regione.emilia-romagna.it/issues/32724) Modificare dimensioni campo Ente 
- [#31953](https://parermine.regione.emilia-romagna.it/issues/31953) modifica al messaggio della pop up quando si setta un oggetto come non risolubile
- [#31648](https://parermine.regione.emilia-romagna.it/issues/31648) Aggiungere nuovi parametri "di sistema" passati da ping a kettle 
- [#31639](https://parermine.regione.emilia-romagna.it/issues/31639) PING Possibilità di modificare l'xml delle UD in errore nel versamento a SACER
- [#31255](https://parermine.regione.emilia-romagna.it/issues/31255) Modifiche alla pagina DETTAGLIO TRASFORMAZIONE per inserire oggetto generato
- [#31151](https://parermine.regione.emilia-romagna.it/issues/31151) SU - personalizzazione del messaggio di errore nel caso di assenza di documenti firmati digitalmente
- [#27691](https://parermine.regione.emilia-romagna.it/issues/27691) Introduzione della motivazione negli annullamenti 
- [#24125](https://parermine.regione.emilia-romagna.it/issues/24125) Portare alla 1.5 il client di versamento a Sacer NO_ZIP
- [#15213](https://parermine.regione.emilia-romagna.it/issues/15213) Annullamento versamenti UD: valorizzare il campo Utente con l'utente che esegue l'operazione

## 5.2.0 (15-05-2024)

### Bugfix: 3
- [#31483](https://parermine.regione.emilia-romagna.it/issues/31483) Correzione dell'errore della modifica dei dati di un progetto ricostruzione dopo aver cliccato sul pulsante verifica agenzia
- [#31368](https://parermine.regione.emilia-romagna.it/issues/31368) Gestione dell'errore nell'annullamento versamento ud dalla pagina OGGETTO DERIVANTE DA VERSAMENTI FALLITI
- [#31076](https://parermine.regione.emilia-romagna.it/issues/31076) Risoluzione problemi nell'elaborazione oggetti del job ProducerCodaVers

### Novità: 11
- [#31816](https://parermine.regione.emilia-romagna.it/issues/31816) SISMA - gestione di un oggetto in chiuso_err_vers rimesso in versamento
- [#31651](https://parermine.regione.emilia-romagna.it/issues/31651) SU - gestione di un oggetto in chiuso_err_vers rimesso in versamento
- [#31256](https://parermine.regione.emilia-romagna.it/issues/31256) Modifica alla pagina Monitoraggio server trasformazioni
- [#31134](https://parermine.regione.emilia-romagna.it/issues/31134) Gestione del caso di oggetto verificato con sessioni precedenti non risolubili che non può essere recuperato
- [#31102](https://parermine.regione.emilia-romagna.it/issues/31102) Modifiche al job verifica hash
- [#30691](https://parermine.regione.emilia-romagna.it/issues/30691) SISMA - Creazione di una pop up di conferma prima di mandare il flusso in stato DA_RIVEDERE
- [#30015](https://parermine.regione.emilia-romagna.it/issues/30015) SISMA - pulsante per svuotare i campi nella maschera di ricerca
- [#30014](https://parermine.regione.emilia-romagna.it/issues/30014) SU - pulsante per svuotare i campi nella maschera di ricerca
- [#27880](https://parermine.regione.emilia-romagna.it/issues/27880) Gestione lunghezza nomi oggetto
- [#27292](https://parermine.regione.emilia-romagna.it/issues/27292) rendere visibile in interfaccia l'id dello strumento urbanistico
- [#26942](https://parermine.regione.emilia-romagna.it/issues/26942) Modifica al comportamento della pagina dettaglio oggetto a seguito di conferma di annullamento

## 5.1.0 (16-02-2024)

### Bugfix: 7
- [#31059](https://parermine.regione.emilia-romagna.it/issues/31059) Correzione nome parametri del WS RecStatoOggettoPing
- [#30844](https://parermine.regione.emilia-romagna.it/issues/30844) SISMA mancata compilazione del campo soggetto attuatore 
- [#30058](https://parermine.regione.emilia-romagna.it/issues/30058) Semplificazione della gestione dell'errore nel job di Esegui Trasformazione
- [#29901](https://parermine.regione.emilia-romagna.it/issues/29901) Correzione del funzionamento del link diretto alla pagina gestione JOB
- [#29611](https://parermine.regione.emilia-romagna.it/issues/29611) SU - Correzione dell'attivazione dell'editing quando si clicca recupera errori
- [#27920](https://parermine.regione.emilia-romagna.it/issues/27920) Correzione della sparizione del campo Controllo Hash e del funzionamento del pulsante indietro nella pagina Dettaglio Tipo Oggetto 
- [#27161](https://parermine.regione.emilia-romagna.it/issues/27161) Modifiche al report di trasformazione

### Novità: 11
- [#31058](https://parermine.regione.emilia-romagna.it/issues/31058) Aggiornamento della foto del Tipo oggetto
- [#31022](https://parermine.regione.emilia-romagna.it/issues/31022) Sostituzione del modulo popup in SISMA
- [#30955](https://parermine.regione.emilia-romagna.it/issues/30955) Ottimizzazione riepilogo versamenti
- [#30935](https://parermine.regione.emilia-romagna.it/issues/30935) SISMA - Ampliare le casistiche di errore nell'interfaccia di gestione dei progetti urbanistici
- [#30808](https://parermine.regione.emilia-romagna.it/issues/30808) SISMA e SU - Nomi con caratteri non standard che provocano il fallimento della verifica dei documenti
- [#30791](https://parermine.regione.emilia-romagna.it/issues/30791) Introduzione partizionamento automatico su PING (Versatori)
- [#30343](https://parermine.regione.emilia-romagna.it/issues/30343) Ricerca oggetto: aggiungere filtro di ricerca Note
- [#30209](https://parermine.regione.emilia-romagna.it/issues/30209) Modifica allo stato CHIUSO_ERR_RECUPERABILE (EX CHIUSO_ERR_TIMEOUT) per includere gli errori 666
- [#30208](https://parermine.regione.emilia-romagna.it/issues/30208) Modifica alla gestione dello stato CHIUSO_ERR_RECUPERABILE (ex CHIUSO_ERR_TIMEOUT)
- [#27037](https://parermine.regione.emilia-romagna.it/issues/27037) Aggiunta della colonna UD nella LISTA OGGETTI GENERATI DA TRASFORMAZIONE
- [#26979](https://parermine.regione.emilia-romagna.it/issues/26979) Riepilogo versamenti - aggiunta campo IdOggetto e Nome oggetto 

## 5.0.0 (26-01-2024)

### Novità: 2
- [#30797](https://parermine.regione.emilia-romagna.it/issues/30797) Aggiornamento a Spring 5 
- [#14221](https://parermine.regione.emilia-romagna.it/issues/14221) Refactor sulla tipologia di transazioni utilizzate nella fase di produzione e consumo del messaggi delle code JMS

## 4.24.2 (19-01-2024)

### Bugfix: 1
- [#31090](https://parermine.regione.emilia-romagna.it/issues/31090) Correzione bug che In Strumenti urbanistici mostra il pulsante versamento anche negli stati errati

## 4.24.1 (09-01-2024)

### Bugfix: 1
- [#31031](https://parermine.regione.emilia-romagna.it/issues/31031) Corretta la mancata visualizzazione del link download rapporti di versamento per strumenti urbanistici

## 4.24.0 (08-01-2024)

### Novità: 6
- [#30939](https://parermine.regione.emilia-romagna.it/issues/30939) Eliminato il parametro VERIFICA_PARTIZIONI e relativo codice
- [#30790](https://parermine.regione.emilia-romagna.it/issues/30790) Creazione automatica delle cartelle FTP per un nuovo versatore
- [#30735](https://parermine.regione.emilia-romagna.it/issues/30735) Reso indipendente PING da SIAM
- [#30693](https://parermine.regione.emilia-romagna.it/issues/30693) SISMA - Ord Privati Vincolati - pop-up post caricamento
- [#29956](https://parermine.regione.emilia-romagna.it/issues/29956) Modificata la creazione di versatore tramite import - gestione dei parametri a livello di versatore
- [#29141](https://parermine.regione.emilia-romagna.it/issues/29141) Aggiunta possibilità di start e stop dei job direttamente dal monitoraggio esame  job schedulati

## 4.23.0 (19-12-2023)

### Bugfix: 5
- [#30922](https://parermine.regione.emilia-romagna.it/issues/30922) Modifiche vista riepilogo versamenti
- [#30781](https://parermine.regione.emilia-romagna.it/issues/30781) SISMA - Correggere il comportamento dell'identificativo al variare del tipo atto
- [#29633](https://parermine.regione.emilia-romagna.it/issues/29633) Correzione estrazione nome utente durante l'autenticazione SPID
- [#29080](https://parermine.regione.emilia-romagna.it/issues/29080) Correzione log di errore 
- [#27038](https://parermine.regione.emilia-romagna.it/issues/27038) Inserimento della ricerca smart nella ricerca versatori della pagina Dettaglio versatore per cui si generano oggetti

### Novità: 12
- [#30851](https://parermine.regione.emilia-romagna.it/issues/30851) Creazione endpoint con informazioni sulla versione
- [#30793](https://parermine.regione.emilia-romagna.it/issues/30793) Conversione di POJO in EJB
- [#30627](https://parermine.regione.emilia-romagna.it/issues/30627) SU - Errore critico nell'inserimento di altra fase di elaborazione per limite numero caratteri
- [#29867](https://parermine.regione.emilia-romagna.it/issues/29867) Aggiunto il pulsante "Scarica xml" alla pagina di visualizzazione di una foto del log eventi 
- [#29431](https://parermine.regione.emilia-romagna.it/issues/29431) Integrazione con SPID professionale
- [#27921](https://parermine.regione.emilia-romagna.it/issues/27921) Introduzione del nome normalizzato di un versatore
- [#27802](https://parermine.regione.emilia-romagna.it/issues/27802) Consentire di settare l'errore in caso di oggetto in stato PREPARAZIONE_OGG_IN_CORSO
- [#26715](https://parermine.regione.emilia-romagna.it/issues/26715) Creazione di una pop up informativa quando si setta non risolubile un oggetto
- [#26279](https://parermine.regione.emilia-romagna.it/issues/26279) Strumenti urbanistici - Paginazione dei dati nella home page
- [#26269](https://parermine.regione.emilia-romagna.it/issues/26269) Creazione log strutturato per errori ORA-01400: impossibile inserire NULL in ("SACER_PING"."PIG_INFO_DICOM"."DS_PATIENT_NAME")
- [#26210](https://parermine.regione.emilia-romagna.it/issues/26210) SISMA - Paginazione dei dati nella home page
- [#22064](https://parermine.regione.emilia-romagna.it/issues/22064) Ampliare le casistiche di errore nell'interfaccia di gestione degli strumenti urbanistici

## 4.22.0 (28-11-2023)

### Novità: 2
- [#30832](https://parermine.regione.emilia-romagna.it/issues/30832) Partizionamento automatico versatori: gestione chiave di partizionamento
- [#30831](https://parermine.regione.emilia-romagna.it/issues/30831) Partizionamento automatico versatori: adeguamento DB con migrazione dati

## 4.21.1 (10-11-2023)

### Bugfix: 1
- [#30816](https://parermine.regione.emilia-romagna.it/issues/30816) Correzione bug job prepara_xml
## 4.21.0 (27-10-2023)

### Bugfix: 4
- [#29861](https://parermine.regione.emilia-romagna.it/issues/29861) Correzione impossibilità creazione versatore
- [#29844](https://parermine.regione.emilia-romagna.it/issues/29844) SISMA - correzione anomalia stato progetto verificato post salvataggio
- [#29767](https://parermine.regione.emilia-romagna.it/issues/29767) Correzione falso stato IN_CODA_VERS
- [#28491](https://parermine.regione.emilia-romagna.it/issues/28491) tipo file oggetto non cancellati quando il relativo tipo file è rimosso

### Novità: 9
- [#30039](https://parermine.regione.emilia-romagna.it/issues/30039) Modifica al versamento a PING per la pulizia della cartella FTP
- [#29704](https://parermine.regione.emilia-romagna.it/issues/29704) Modifiche al flusso SISMA - l'annullamento di un progetto SA privato deve andare in DA_VERIFICARE 
- [#29587](https://parermine.regione.emilia-romagna.it/issues/29587) Introduzione AWS S3 sdk2 su ping 
- [#29331](https://parermine.regione.emilia-romagna.it/issues/29331) SISMA - Aggiunta del campo atto nel dettaglio del progetto ricostruzione
- [#29267](https://parermine.regione.emilia-romagna.it/issues/29267) Modifica al filtro AMBITO nella pagina gestione JOB
- [#27632](https://parermine.regione.emilia-romagna.it/issues/27632) SISMA - oggetto non risolubile e annullato non annulla il progetto sisma
- [#27430](https://parermine.regione.emilia-romagna.it/issues/27430) SISMA - gestione dello stato ERRORE
- [#27381](https://parermine.regione.emilia-romagna.it/issues/27381) SISMA - Modifica  della funzione "riporta allo stato bozza" per rimanere sul Dettaglio progetto ricostruzione
- [#27380](https://parermine.regione.emilia-romagna.it/issues/27380) SU - Modifica  della funzione "riporta allo stato bozza" per rimanere sul dettaglio dello strumento

## 4.20.0 (17-08-2023)

### Novità: 1
- [#30023](https://parermine.regione.emilia-romagna.it/issues/30023) SISMA - Modifica al campo soggetto attuatore nel dettaglio del progetto e nell'XML versato

## 4.19.0 (03-08-2023)

### Novità: 2
- [#29976](https://parermine.regione.emilia-romagna.it/issues/29976) SiSMA - Modifica alla gestione del documento principale
- [#29970](https://parermine.regione.emilia-romagna.it/issues/29970) SISMA - Modifica alla gestione del tipo atto

## 4.18.0 (01-08-2023)

### Novità: 1
- [#29663](https://parermine.regione.emilia-romagna.it/issues/29663) Aggiornamento librerie obsolete 2023

## 4.17.1 (20-07-2023)

### Bugfix: 1
- [#29947](https://parermine.regione.emilia-romagna.it/issues/29947) Valorizzazione del campo identificativo delle fasi collegate negli SU

## 4.17.0 (13-07-2023)

### Bugfix: 4
- [#29905](https://parermine.regione.emilia-romagna.it/issues/29905) Correzione della mancata visualizzazione delle icone di download nel wizard sisma
- [#29700](https://parermine.regione.emilia-romagna.it/issues/29700) Correzione della visualizzazione dell'xml in caso di particolare lunghezza 
- [#29623](https://parermine.regione.emilia-romagna.it/issues/29623) Convertite le stringhe dei parametri applicativi in constanti.
- [#28465](https://parermine.regione.emilia-romagna.it/issues/28465) Correzione dello stato dell'oggetto in errore di impronta

### Novità: 2
- [#29667](https://parermine.regione.emilia-romagna.it/issues/29667) Aggiunta in SISMA la gestione della modifica metadati su progetti ritornati in bozza
- [#28570](https://parermine.regione.emilia-romagna.it/issues/28570) Aggiunta la possibilità in SISMA di riportare il progetto in stato BOZZA dallo stato DA_RIVEDERE

## 4.16.0 (05-07-2023)

### Novità: 1
- [#29875](https://parermine.regione.emilia-romagna.it/issues/29875) Configurazione dei timeout per le sessioni di versamento a Sacer

## 4.15.0 (26-06-2023)

### Bugfix: 1
- [#26776](https://parermine.regione.emilia-romagna.it/issues/26776) Rimozione del riferimento a FTP nella pagina  'Versamento completato con successo'.

### Novità: 3
- [#29495](https://parermine.regione.emilia-romagna.it/issues/29495) SU - Modifiche alle altre fasi di elaborazione per mostrare solo strumenti in stato versato
- [#27352](https://parermine.regione.emilia-romagna.it/issues/27352) SU - Gestione della modifica metadati Identificativo su progetti ritornati in bozza 
- [#25199](https://parermine.regione.emilia-romagna.it/issues/25199) Dettaglio oggetto derivante da versamenti: pulsante per mettere in verificato tutte le sessioni

## 4.14.1 (09-06-2023)

### Bugfix: 14
- [#29644](https://parermine.regione.emilia-romagna.it/issues/29644) Correzione falso stato IN_CODA_VERS
- [#29618](https://parermine.regione.emilia-romagna.it/issues/29618) Correzione del mancato offuscamento del parametro di tipo password nel dettaglio del tipo oggetto
- [#29616](https://parermine.regione.emilia-romagna.it/issues/29616) Correzione della mancata visualizzazione dei pulsanti annulla oggetto e annulla versamento ud su sacer se lo stato è CHIUSO OK (WARNING CHIAVE DUPLICATA))
- [#29598](https://parermine.regione.emilia-romagna.it/issues/29598) Correzione dell'incongruenze nella schermata di riepilogo
- [#29594](https://parermine.regione.emilia-romagna.it/issues/29594) Correzione dell'errore critico nella modifica delle note degli oggetti DA TRASFORMARE
- [#28503](https://parermine.regione.emilia-romagna.it/issues/28503) Modifica del pulsante download xml unità documentaria nella pagina DETTAGLIO UNITA' DOCUMENTARIA
- [#28300](https://parermine.regione.emilia-romagna.it/issues/28300) Eliminazione commento all'interno del codice html
- [#27495](https://parermine.regione.emilia-romagna.it/issues/27495) Modificato il messaggio di conferma di annullamento oggetto
- [#27179](https://parermine.regione.emilia-romagna.it/issues/27179) Correzione errore nel salvataggio di un tipo oggetto ZIP con XML SACER
- [#27104](https://parermine.regione.emilia-romagna.it/issues/27104) Mostrata la funzione "cambia versatore" quando si è nelle pagine del menù "Amministrazione delle trasformazioni"
- [#27090](https://parermine.regione.emilia-romagna.it/issues/27090) Modifica dell'ordinamento versioni precedenti di una trasformazione
- [#26890](https://parermine.regione.emilia-romagna.it/issues/26890) Correzione dell'errore XF_ERR che si ottiene dopo aver rimesso l'oggetto in DA TRASFORMARE 
- [#26730](https://parermine.regione.emilia-romagna.it/issues/26730) Correzione errore pagina grigia dopo il download
- [#26678](https://parermine.regione.emilia-romagna.it/issues/26678) Aggiunta dello stato CHIUSO_ERR_SCHED nella lookup Dettaglio stato
## 4.9.10 (16-09-2022)

### Bugfix: 1
- [#27702](https://parermine.regione.emilia-romagna.it/issues/27702) Aggiornamento loghi

## 4.9.9 (15-09-2022)

### Bugfix: 3
- [#27682](https://parermine.regione.emilia-romagna.it/issues/27682) Gestione di errori nel salvataggio di una trasformazione
- [#27677](https://parermine.regione.emilia-romagna.it/issues/27677) Gestione dell'errore nell'esecuzione del job prepara xml sacer
- [#27673](https://parermine.regione.emilia-romagna.it/issues/27673) Gestione dell'errore nel caricamento di un file nel client sisma

## 4.9.8 (09-09-2022)

### Bugfix: 5
- [#27651](https://parermine.regione.emilia-romagna.it/issues/27651) Correzione dell'errore inatteso caricando un oggetto da trasformare maggiore di 10 mb
- [#27631](https://parermine.regione.emilia-romagna.it/issues/27631) Correzione dell'errore critico avviando la Ricerca eventi
- [#27630](https://parermine.regione.emilia-romagna.it/issues/27630) Correzione dell'errore critico aprendo la pagina Monitoraggio server trasformazioni
- [#27627](https://parermine.regione.emilia-romagna.it/issues/27627) Correzione dell'Errore sul job allineamento organizzazioni
- [#27527](https://parermine.regione.emilia-romagna.it/issues/27527) Corretta la gestione delle dipendenze per il WS ReplicaUtente

## 4.14.0 (24-05-2023)

### Bugfix: 1
- [#29527](https://parermine.regione.emilia-romagna.it/issues/29527) Corretta la mancata visualizzazione icona download dettaglio SU e Sisma

### Novità: 14
- [#28877](https://parermine.regione.emilia-romagna.it/issues/28877) Modificato il tipo oggetto per gli oggetti in stato TRASFORMAZIONE NON ATTIVA o DA TRASFORMARE
- [#28358](https://parermine.regione.emilia-romagna.it/issues/28358) adeguamento SAML per regione PUGLIA
- [#27970](https://parermine.regione.emilia-romagna.it/issues/27970) Aggiunto il controllo formato sul campo numero negli SU
- [#27543](https://parermine.regione.emilia-romagna.it/issues/27543) Aggiunta in Ricerca Versatore la sezione di ricerca "Enti di appartenenza" 
- [#27434](https://parermine.regione.emilia-romagna.it/issues/27434) Resi case unsensitive i campi NOME e DESCRIZIONE nella Ricerca oggetto 
- [#27422](https://parermine.regione.emilia-romagna.it/issues/27422) Modificato il funzionamento del campo anno nella ricerca SU
- [#27405](https://parermine.regione.emilia-romagna.it/issues/27405)  Aggiunto logging accessi SPID non autorizzati
- [#27337](https://parermine.regione.emilia-romagna.it/issues/27337) Ridenominato il ws di Recupero stato oggetto in RecStatoOggettoPing
- [#27177](https://parermine.regione.emilia-romagna.it/issues/27177) Inserimento della ricerca smart nella ricerca struttura in dettaglio tipo oggetto
- [#26849](https://parermine.regione.emilia-romagna.it/issues/26849) Aggiunto il campo IdOggetto alla Ricerca oggetto
- [#26617](https://parermine.regione.emilia-romagna.it/issues/26617) Aggiunta della colonna "Categoria Oggetto" nella lista "Tipi Oggetto"
- [#26208](https://parermine.regione.emilia-romagna.it/issues/26208) Integrata la gestione di SPID della Puglia nell'attuale gestione SPID in RER
- [#25727](https://parermine.regione.emilia-romagna.it/issues/25727) Aggiunta la colonna tipo versatore in gestione versatori
- [#25594](https://parermine.regione.emilia-romagna.it/issues/25594) Modificata la gestione del registro parametri

## 4.13.0 (08-05-2023)

### Novità: 1
- [#18355](https://parermine.regione.emilia-romagna.it/issues/18355) Creazione pacchetto unico per Ping

## 4.12.1 (04-05-2023)

### Bugfix: 9
- [#29470](https://parermine.regione.emilia-romagna.it/issues/29470) Correzione alla selezione del campo stato progetto in progetti in bozza
- [#29391](https://parermine.regione.emilia-romagna.it/issues/29391) Gestione della priorità di versamento del JOB PREPARA XML SACER
- [#28343](https://parermine.regione.emilia-romagna.it/issues/28343) Gestione dell'errore in controlli notifica trasferimento file
- [#27683](https://parermine.regione.emilia-romagna.it/issues/27683) Gestione permessi dei file per la cartella dell'FTP
- [#27423](https://parermine.regione.emilia-romagna.it/issues/27423) Correzione dell'errore nel salvataggio di una modifica in dettaglio versamento
- [#27302](https://parermine.regione.emilia-romagna.it/issues/27302) Correzione del messaggio quando si riporta in bozza uno strumento
- [#27228](https://parermine.regione.emilia-romagna.it/issues/27228) Correzione della messaggio nel versamento di un oggetto  già presente in stato "errore versamento a sacer"
- [#27088](https://parermine.regione.emilia-romagna.it/issues/27088) Rimosso il pulsante "Versa in Agenzia" se il progetto ricostruzione è in stato "verificato"
- [#26684](https://parermine.regione.emilia-romagna.it/issues/26684) Correzione dell'errore nell'inserimento di un tipo oggetto ZIP CON XML SACER

## 4.12.0 (20-04-2023)

### Bugfix: 2
- [#28344](https://parermine.regione.emilia-romagna.it/issues/28344) Valorizzazione del tipo calcolo hash nel tipo file
- [#27350](https://parermine.regione.emilia-romagna.it/issues/27350) Correzione gestione concorrenza aggiornamenti stato degli Oggetti IN_CODA_VERS 

### Novità: 5
- [#27674](https://parermine.regione.emilia-romagna.it/issues/27674) Nuova pagina gestione JOB 
- [#27321](https://parermine.regione.emilia-romagna.it/issues/27321) Introduzione della priorità di versamento di un oggetto ZIP_CON_XML_SACER e NO_ZIP
- [#26937](https://parermine.regione.emilia-romagna.it/issues/26937) Eliminazione della relazione tra fase progettuale e tipo di atto
- [#26936](https://parermine.regione.emilia-romagna.it/issues/26936) Eliminazione della relazione tra Tipo strumento urbanistico e tipo di atto
- [#26211](https://parermine.regione.emilia-romagna.it/issues/26211) Creazione nuovo stato WARNING_CHIAVE_DUPLICATA
## 4.11.0 (03-02-2023)

### Novità: 2
- [#28305](https://parermine.regione.emilia-romagna.it/issues/28305) Modifica alla cancellazione in seguito a incompatibilità
- [#27751](https://parermine.regione.emilia-romagna.it/issues/27751) Aggiornamento framework javascript (JQuery) e di alcuni plugin

## 4.10.0 (19-12-2022)

### Novità: 2
- [#27751](https://parermine.regione.emilia-romagna.it/issues/27751) Aggiornamento framework javascript (JQuery) e di alcuni plugin
- [#27356](https://parermine.regione.emilia-romagna.it/issues/27356) Aggiornamento librerie obsolete 2022

## 4.9.11 (05-12-2022)

### Bugfix: 6
- [#27901](https://parermine.regione.emilia-romagna.it/issues/27901) Correzione configurazione coda ProducerCodaVersQueue
- [#27648](https://parermine.regione.emilia-romagna.it/issues/27648) Correzione errore Null pointer per InviaOggettiGeneratiAPing
- [#27347](https://parermine.regione.emilia-romagna.it/issues/27347) Correzione  al campo calcolato ID del versatore nel versatore per cui si generano oggetti  
- [#27271](https://parermine.regione.emilia-romagna.it/issues/27271) Correzione alla validazione dell'indice oggetto al versamento
- [#27046](https://parermine.regione.emilia-romagna.it/issues/27046) Correzione ordinamento lista progetti per dimensione dei progetti ricostruzione
- [#12248](https://parermine.regione.emilia-romagna.it/issues/12248) Correzione errori del servizio di recupero di unità documentaria da PING

### Novità: 4
- [#27427](https://parermine.regione.emilia-romagna.it/issues/27427) Modifica alla label "info" nella pagina dettaglio oggetto
- [#25815](https://parermine.regione.emilia-romagna.it/issues/25815) Reso visibile un ambiente appena creato
- [#25814](https://parermine.regione.emilia-romagna.it/issues/25814) Creazione automatica del Tipo file  per Tipo oggetto
- [#25510](https://parermine.regione.emilia-romagna.it/issues/25510) Trasformazione dei messaggi sulle code JMS da tipo object verso tipo stringa

## 4.9.10 (16-09-2022)

### Bugfix: 1
- [#27702](https://parermine.regione.emilia-romagna.it/issues/27702) Aggiornamento loghi

## 4.9.9 (15-09-2022)

### Bugfix: 3
- [#27682](https://parermine.regione.emilia-romagna.it/issues/27682) Gestione di errori nel salvataggio di una trasformazione
- [#27677](https://parermine.regione.emilia-romagna.it/issues/27677) Gestione dell'errore nell'esecuzione del job prepara xml sacer
- [#27673](https://parermine.regione.emilia-romagna.it/issues/27673) Gestione dell'errore nel caricamento di un file nel client sisma

## 4.9.8 (09-09-2022)

### Bugfix: 5
- [#27651](https://parermine.regione.emilia-romagna.it/issues/27651) Correzione dell'errore inatteso caricando un oggetto da trasformare maggiore di 10 mb
- [#27631](https://parermine.regione.emilia-romagna.it/issues/27631) Correzione dell'errore critico avviando la Ricerca eventi
- [#27630](https://parermine.regione.emilia-romagna.it/issues/27630) Correzione dell'errore critico aprendo la pagina Monitoraggio server trasformazioni
- [#27627](https://parermine.regione.emilia-romagna.it/issues/27627) Correzione dell'Errore sul job allineamento organizzazioni
- [#27527](https://parermine.regione.emilia-romagna.it/issues/27527) Corretta la gestione delle dipendenze per il WS ReplicaUtente

## 4.9.7.1 (13-07-2022)

### Bugfix: 1
- [#27442](https://parermine.regione.emilia-romagna.it/issues/27442) Correzione di un errore critico nell'apertura del dettaglio di un oggetto DPI

## 4.9.7 (16-06-2022)

### Bugfix: 5
- [#27254](https://parermine.regione.emilia-romagna.it/issues/27254) Correzione dell'errore critico sulla funzionalità sui filtri nel client SISMA
- [#27157](https://parermine.regione.emilia-romagna.it/issues/27157) Correzione gestione concorrenza aggiornamenti stato degli Oggetti IN_CODA_VERS 
- [#26713](https://parermine.regione.emilia-romagna.it/issues/26713) Modifica al contatore dell'upload del file in versamento oggetto
- [#26687](https://parermine.regione.emilia-romagna.it/issues/26687) Correzione del messaggio di superamento dimensione massima
- [#26401](https://parermine.regione.emilia-romagna.it/issues/26401) SISMA - conservare i file quando si riporta in stato di bozza

### Novità: 6
- [#27303](https://parermine.regione.emilia-romagna.it/issues/27303) Migliorie al report della verifica documenti in SU e SISMA
- [#26891](https://parermine.regione.emilia-romagna.it/issues/26891) Modifica alla pagina Ricerca oggetti - aggiunta della modalità di gestione figli
- [#26777](https://parermine.regione.emilia-romagna.it/issues/26777) Strumenti Urbanistici - Modifica intestazioni tabelle caricamento dati
- [#26583](https://parermine.regione.emilia-romagna.it/issues/26583) Tracciamento nei log del progresso della preparazione oggetti dopo la trasformazione.
- [#26302](https://parermine.regione.emilia-romagna.it/issues/26302) Interventi di modifica interfaccia per aggiungere il campo calcolato ID del versatore nel Dettaglio Tipo Oggetto
- [#25733](https://parermine.regione.emilia-romagna.it/issues/25733) Miglioramento della modalità di ricerca di alcuni campi

## 4.9.6 (30-05-2022)

### Bugfix: 1
- [#27281](https://parermine.regione.emilia-romagna.it/issues/27281) Correzione SISMA SA Pubblico: dopo aver riportato in BOZZA viene rimesso da trasformare l'oggetto dell'agenzia e non quello del SA pubblico

## 4.9.5

### Bugfix: 2
- [#27267](https://parermine.regione.emilia-romagna.it/issues/27267) SISMA - correzione alla mancata visualizzazione dei campi di verifica agenzia
- [#27187](https://parermine.regione.emilia-romagna.it/issues/27187) Eliminazione commento all'interno del codice html

### Novità: 1
- [#27193](https://parermine.regione.emilia-romagna.it/issues/27193) SISMA - estensione del campo identificativo

## 4.9.4 (19-05-2022)

### Bugfix: 4
- [#27150](https://parermine.regione.emilia-romagna.it/issues/27150) Correzione label Indice oggetto in Versamento oggetto
- [#26765](https://parermine.regione.emilia-romagna.it/issues/26765) Sisma - Ricerca - Eliminazione case sensitive
- [#26763](https://parermine.regione.emilia-romagna.it/issues/26763) Strumenti urbanistici - "Ricerca" eliminazione case sensitive
- [#26400](https://parermine.regione.emilia-romagna.it/issues/26400) SU - conservare i file quando si riporta in stato di bozza

### Novità: 5
- [#27034](https://parermine.regione.emilia-romagna.it/issues/27034) Gestione della validazione dell'indice oggetto al versamento e adeguamento interfaccia
- [#26398](https://parermine.regione.emilia-romagna.it/issues/26398) Gestione dell'annullamento oggetto anche per i progetti Sisma
- [#26290](https://parermine.regione.emilia-romagna.it/issues/26290) SISMA - aggiunta nuova colonna al cruscotto
- [#26267](https://parermine.regione.emilia-romagna.it/issues/26267) Sisma - creare un export degli errori di caricamento per errori formali sul contenuto
- [#25704](https://parermine.regione.emilia-romagna.it/issues/25704) Strumenti urbanistici - creare un export degli errori di caricamento per errori formali sul contenuto

## 4.9.3 (27-04-2022)

### Bugfix: 1
- [#27156](https://parermine.regione.emilia-romagna.it/issues/27156) correzione al funzionamento del versamento oggetto con object storage

## 4.9.2 (21-04-2022)

### Bugfix: 2
- [#27016](https://parermine.regione.emilia-romagna.it/issues/27016) Gestione dell'assenza del parametro dimensione massima per il versamento su object storage
- [#26970](https://parermine.regione.emilia-romagna.it/issues/26970) Estensione del limite caratteri nel campo "versione XML" della pagina "versamento oggetto"

### Novità: 3
- [#27039](https://parermine.regione.emilia-romagna.it/issues/27039) Download di file di un oggetto in caso di versamento con indice xml 
- [#26971](https://parermine.regione.emilia-romagna.it/issues/26971) Modifica della label "versione XML" in "Indice Oggetto"
- [#26969](https://parermine.regione.emilia-romagna.it/issues/26969) Possibiltà di aggiungere un file xml con un oggetto di tipo DA TRASFORMARE

## 4.9.1 (14-04-2022)

### Bugfix: 1
- [#27124](https://parermine.regione.emilia-romagna.it/issues/27124) Aggiunta di log sull'aggiornamento di stato di una PigSessioneObject

### Novità: 1
- [#26667](https://parermine.regione.emilia-romagna.it/issues/26667) Aggiornamento librerie obsolete primo quadrimestre 2021

## 4.9.0 (25-03-2022)

### Bugfix: 3
- [#26944](https://parermine.regione.emilia-romagna.it/issues/26944) Errore nella cancellazione di uno strumento urbanistico
- [#26917](https://parermine.regione.emilia-romagna.it/issues/26917) Errore critico nell'aprire il dettaglio di un oggetto DPI
- [#26080](https://parermine.regione.emilia-romagna.it/issues/26080) Oggetto bloccato in PREPARAZIONE_OGG_IN_CORSO per omonimia con pacchetto già trasformato 

### Novità: 2
- [#26688](https://parermine.regione.emilia-romagna.it/issues/26688) Introduzione del parametro dimensione massima per il versamento su object storage
- [#25743](https://parermine.regione.emilia-romagna.it/issues/25743) Modifiche alla verifica dei file caricati - inclusione del nome del file zip nel controllo di lunghezza path

## 4.8.7

### Bugfix: 5
- [#26882](https://parermine.regione.emilia-romagna.it/issues/26882) Modifica all'ordinamento della colonna numero ud nella lista dei risultati 
- [#26877](https://parermine.regione.emilia-romagna.it/issues/26877) Correzione dell'esito di annullamento in caso positivo
- [#26867](https://parermine.regione.emilia-romagna.it/issues/26867) Sisma - modifica sul controllo sui campi chiave dell'agenzia
- [#26848](https://parermine.regione.emilia-romagna.it/issues/26848) Oggetti in stato IN_CODA_VERS
- [#26744](https://parermine.regione.emilia-romagna.it/issues/26744) Correzione del comportamento del pulsante "Versa oggetto in ping"

### Novità: 1
- [#26200](https://parermine.regione.emilia-romagna.it/issues/26200) Modifica alla funzione "annulla versamenti unità documentarie" - scelta dell'utente

## 4.8.6 (24-02-2022)

### Bugfix: 1
- [#25995](https://parermine.regione.emilia-romagna.it/issues/25995) creazione ambiente: devono essere compilati tutti i campi obbligatori

### Novità: 4
- [#26216](https://parermine.regione.emilia-romagna.it/issues/26216) Creare una popup di richiesta conferma quando si preme "annulla versamenti unità documentarie"
- [#25784](https://parermine.regione.emilia-romagna.it/issues/25784) Modificare la label Tipo SIP in Categoria oggetto
- [#25555](https://parermine.regione.emilia-romagna.it/issues/25555) Lista oggetti monitoraggio - ordinamento di default per data stato corrente 
- [#13062](https://parermine.regione.emilia-romagna.it/issues/13062) Dettaglio oggetto: presentare gli esiti della richiesta di annullamento a SACER

## 4.8.5

### Bugfix: 10
- [#26714](https://parermine.regione.emilia-romagna.it/issues/26714) Correzione di errore sul funzionamento del pulsante SET annullato da trasformare
- [#26447](https://parermine.regione.emilia-romagna.it/issues/26447) Correzione del problema di cancellazione dei file su NFS 
- [#26429](https://parermine.regione.emilia-romagna.it/issues/26429) [problema rapporto di versamento] COMGUASTALLA_AOO1_SU
- [#26246](https://parermine.regione.emilia-romagna.it/issues/26246) Gestione della concorrenza in caso di risposte di versamento - introduzione di lock
- [#26205](https://parermine.regione.emilia-romagna.it/issues/26205) SISMA - Correzione del messaggio della popup di progetto già presente
- [#26019](https://parermine.regione.emilia-romagna.it/issues/26019) Correzione di un problema nella ricerca di gestione versatori
- [#25781](https://parermine.regione.emilia-romagna.it/issues/25781) Correzione della visualizzazione del pulsante recupera errori su strumenti urbanistici.
- [#25605](https://parermine.regione.emilia-romagna.it/issues/25605) Ripristino del versamento manuale di un oggetto figlio generato da trasformazione
- [#25054](https://parermine.regione.emilia-romagna.it/issues/25054) Risoluzione problema URI Encoding
- [#13237](https://parermine.regione.emilia-romagna.it/issues/13237) Indagine per risoluzione errore lettere accentate in nome file inviato tramite pagina di "Versamento Oggetto" 

### Novità: 11
- [#26278](https://parermine.regione.emilia-romagna.it/issues/26278) Strumenti urbanistici - Ricerca parametrizzata in Home Page dei progetti
- [#26204](https://parermine.regione.emilia-romagna.it/issues/26204) Sisma - controllo sui campi chiave dell'agenzia
- [#26165](https://parermine.regione.emilia-romagna.it/issues/26165) Sisma - Ricerca parametrizzata in Home Page dei progetti
- [#26162](https://parermine.regione.emilia-romagna.it/issues/26162) Interventi di modifica interfaccia per aggiungere il campo calcolato ID del versatore
- [#26156](https://parermine.regione.emilia-romagna.it/issues/26156) Modifica alla pagina Ricerca oggetti - aggiunta del numero ud nella lista dei risultati 
- [#26012](https://parermine.regione.emilia-romagna.it/issues/26012) Indicazione in interfaccia del percorso dei file di un oggetto versato
- [#25602](https://parermine.regione.emilia-romagna.it/issues/25602) Implementazione del versamento su object storage per gli oggetti ZIP CON XML SACER
- [#25601](https://parermine.regione.emilia-romagna.it/issues/25601) Implementazione del versamento su object storage sulla pagina Versamento oggetto
- [#25135](https://parermine.regione.emilia-romagna.it/issues/25135) Associazione utente SPID con anagrafica utenti per le applicazioni PING
- [#24085](https://parermine.regione.emilia-romagna.it/issues/24085) Strumenti urbanistici - rendere modificabile il campo "Descrizione" anche dopo il versamento
- [#18124](https://parermine.regione.emilia-romagna.it/issues/18124) Distinguere negli stati gli errori di versamento da quelli di preparazione SIP

## 4.8.4 (30-12-2021)

### Bugfix: 1
- [#26516](https://parermine.regione.emilia-romagna.it/issues/26516) Correzione rapporto di versamento indirizzo ip sistema versante

## 4.8.3 (23-11-2021)

### Novità: 3
- [#26306](https://parermine.regione.emilia-romagna.it/issues/26306) adeguamento PING a seguito della MEV 23905 di SIAM
- [#26303](https://parermine.regione.emilia-romagna.it/issues/26303) Dettaglio utente: Inserimento del certificato e del flag TIPO_AUTH per gli utenti automi
- [#25774](https://parermine.regione.emilia-romagna.it/issues/25774) Gestione di diversi livelli di accesso con credenziali SPID 

## 4.8.2.3 (20-09-2021)

### Bugfix: 1
- [#25899](https://parermine.regione.emilia-romagna.it/issues/25899) Correzione WS ricercaDiario - hibernate

## 4.8.2.2 (13-09-2021)

### Bugfix: 1
- [#25847](https://parermine.regione.emilia-romagna.it/issues/25847) Correzione errore eliminazione ente di appartenenza versatore

## 4.8.2.1 (09-09-2021)

### Bugfix: 1
- [#25824](https://parermine.regione.emilia-romagna.it/issues/25824) Correzione inserimento nuovo versatore

### Novità: 1
- [#25257](https://parermine.regione.emilia-romagna.it/issues/25257) Modifica chiamate in GET a rimozione oggetti, vulnerabili al cross site request forgery.

## 4.8.2 (03-09-2021)

### Novità: 1
- [#25776](https://redmine.ente.regione.emr.it/issues/25776) Versione hibernate della 4.7.2

## 4.7.2 (03-09-2021)

### Bugfix: 2
- [#25551](https://redmine.ente.regione.emr.it/issues/25551) Offuscamento campo parametro di tipo password solo in visualizzazione
- [#15733](https://redmine.ente.regione.emr.it/issues/15733) Non registrazione xml per unita doc di sessione
- [#25776](https://parermine.regione.emilia-romagna.it/issues/25776) Versione hibernate della 4.7.2

## 4.7.1

### Bugfix: 5
- [#25499](https://parermine.regione.emilia-romagna.it/issues/25499) Correzione gestione errata oggetto vuoto
- [#25485](https://parermine.regione.emilia-romagna.it/issues/25485) Monitoraggio trasformazioni: non viene aggiornata la lista delle trasformazioni
- [#25484](https://parermine.regione.emilia-romagna.it/issues/25484) Consentire il versamento di oggetti diversi da zip anche su object storage
- [#25426](https://parermine.regione.emilia-romagna.it/issues/25426) Correzione accesso voce di menu "strumenti urbanistici" con versatore non abilitato a " strumenti  urbanistici"
- [#20309](https://parermine.regione.emilia-romagna.it/issues/20309) Dettaglio oggetto derivante da versamenti falliti: errore critico quando si verificano sessioni in più pagine

### Novità: 2
- [#25357](https://parermine.regione.emilia-romagna.it/issues/25357) Gestione del campo priorità nei versamenti oggetto
- [#22933](https://parermine.regione.emilia-romagna.it/issues/22933) Campo parametro di tipo password

## 4.7.0

### EVO: 1
- [#22311](https://parermine.regione.emilia-romagna.it/issues/22311) Memorizzare gli oggetti da trasformare su object storage

### Bugfix: 5
- [#25207](https://parermine.regione.emilia-romagna.it/issues/25207) Errore in salvataggio dettaglio oggetto se priorità vuota
- [#23539](https://parermine.regione.emilia-romagna.it/issues/23539) Mettere in trasformazione un oggetto per ogni istanza di kettle-server
- [#23269](https://parermine.regione.emilia-romagna.it/issues/23269) Gestione errata oggetto vuoto
- [#21589](https://parermine.regione.emilia-romagna.it/issues/21589) Disaccoppiare la gestione dei dati della trasformazione tra ping e kettle-server
- [#17589](https://parermine.regione.emilia-romagna.it/issues/17589) Dettaglio UD di sessione: errore sulla presentazione

### Novità: 7
- [#24717](https://parermine.regione.emilia-romagna.it/issues/24717) Modifiche al job di Verifica Hash per supportare oggetti memorizzati su object storage
- [#24582](https://parermine.regione.emilia-romagna.it/issues/24582) Aggiunta proprietà di sistema per gestire abilitazione/disabilitazione di ObjectStorage e funzionalità collegate
- [#22000](https://parermine.regione.emilia-romagna.it/issues/22000) Modifica job “Esegui trasformazione” di Sacerping per gestione identificativo S3 dell'oggetto da trasformare
- [#21995](https://parermine.regione.emilia-romagna.it/issues/21995) Modifica processo di "Versamento oggetto da trasformare” di Sacerping al fine di memorizzare l'oggetto da trasformare su Object Storage
- [#20421](https://parermine.regione.emilia-romagna.it/issues/20421) Gestire il cambio ambiente di un Versatore da interfaccia
- [#16170](https://parermine.regione.emilia-romagna.it/issues/16170) Informazioni sulle trasformazioni in corso
- [#15177](https://parermine.regione.emilia-romagna.it/issues/15177) Versamento oggetti da trasformare anche in altri formati oltre lo ZIP

## 4.6.6 (22-07-2021)

### Bugfix: 2
- [#25505](https://parermine.regione.emilia-romagna.it/issues/25505) Progetti ricostruzione: correzione modalità di visualizzazione dopo utilizzo funzione "salva bozza"
- [#25477](https://parermine.regione.emilia-romagna.it/issues/25477) Progetti ricostruzione: correzione errore download Rapporto di versamento Agenzia

## 4.6.5 (21-07-2021)

### Bugfix: 1
- [#25468](https://parermine.regione.emilia-romagna.it/issues/25468) Progetti ricostruzione: correzione modalità di valorizzazione dei dati dell'intervento

## 4.6.4 (26-05-2021)

### Novità: 1
- [#24897](https://parermine.regione.emilia-romagna.it/issues/24897) Progetti ricostruzione: modifiche ai messaggi di alert

## 4.6.3 (07-05-2021)

### Bugfix: 2
- [#24961](https://parermine.regione.emilia-romagna.it//issues/24961) Strumenti urbanistici: correzione errori su modifiche interfaccia
- [#24891](https://parermine.regione.emilia-romagna.it//issues/24891) Correzione della funzionalità per settare a non risolubile una sessione annullata

## 4.6.2 (30-04-2021)

### Bugfix: 1
- [#24874](https://parermine.regione.emilia-romagna.it//issues/24874) Strumenti urbanistici: disabilitazione pulsante versamento degli oggetti in stato IN ELABORAZIONE

### Novità: 3
- [#24938](https://parermine.regione.emilia-romagna.it//issues/24938) Adeguamenti progetto sisma 
- [#24924](https://parermine.regione.emilia-romagna.it//issues/24924) Progetti ricostruzione: aggiungere controllo caratteri su file contenuti negli zip
- [#24857](https://parermine.regione.emilia-romagna.it//issues/24857) Modifiche interfaccia Progetti SISMA

## 4.6.1 (20-04-2021)

### Bugfix: 5
- [#24782](https://parermine.regione.emilia-romagna.it//issues/24782) Strumenti urbanistici: disabilitazione pulsante versamento dopo il versamento dell'oggetto
- [#24092](https://parermine.regione.emilia-romagna.it//issues/24092) Generazione javadoc
- [#23682](https://parermine.regione.emilia-romagna.it//issues/23682) Correzione errore critico in caso di annullamento di un oggetto
- [#21777](https://parermine.regione.emilia-romagna.it//issues/21777) Problema scarico xsd dati specifici tipo oggetto
- [#21308](https://parermine.regione.emilia-romagna.it//issues/21308) Correzione della funzionalità per settare a non risolubile una sessione annullata

### Novità: 2
- [#24256](https://parermine.regione.emilia-romagna.it//issues/24256) Abilitata l'associazione di Versatori a Soggetti attuatori
- [#23511](https://parermine.regione.emilia-romagna.it//issues/23511) Inserimento attributo/selettore messaggio JMS con indicazione dell'applicazione

## 4.6.0 (22-03-2021)

### Novità: 1
- [#24417](https://parermine.regione.emilia-romagna.it//issues/24417) Versamento Progetti Sisma - Fase2 

## 4.5.6 (10-03-2021)

### Bugfix: 1
- [#24999](https://parermine.regione.emilia-romagna.it//issues/24999) correzione errore in fase di salvataggio dei dati dell'agenzia di un progetto 

### Novità: 1
- [#23183](https://parermine.regione.emilia-romagna.it//issues/23183) Versamento Progetti sisma - Fase1

## 4.5.5

### Bugfix: 2
- [#23771](https://parermine.regione.emilia-romagna.it//issues/23771) Pulsante versamento presente anche su Strumenti urbanistici già versati
- [#23636](https://parermine.regione.emilia-romagna.it//issues/23636) Modifica tipo oggetto non possibile

## 4.5.4

### Novità: 2
- [#23598](https://parermine.regione.emilia-romagna.it//issues/23598) Rimozione logo IBACN per passaggio a RER
- [#23549](https://parermine.regione.emilia-romagna.it//issues/23549) Introdurre campo Note su Ambiente

## 4.5.3

### Bugfix: 2
- [#23518](https://parermine.regione.emilia-romagna.it//issues/23518) Oggetto settato a CHIUSO_ERR_TRASFORMAZIONE: salvare la sessione con flag Verificato
- [#23517](https://parermine.regione.emilia-romagna.it//issues/23517) Path errato su versatori duplicati o importati

### Novità: 1
- [#23551](https://parermine.regione.emilia-romagna.it//issues/23551)  Controllo librerie obsolete terzo quadrimestre 2020

### SUE: 1
- [#22652](https://parermine.regione.emilia-romagna.it//issues/22652) Rilascio PING in preproduzione e produzione  4.5.3  (versione Kettle multi-istanza)

## 4.5.2

### Bugfix: 2
- [#23442](https://parermine.regione.emilia-romagna.it//issues/23442) Errore nell'esportazione e nell'importazione di un Versatore
- [#23440](https://parermine.regione.emilia-romagna.it//issues/23440) Errore nel recupero errori di trasformazione (Chiuso err trasformazione) su oggetti in warning

### Novità: 2
- [#23441](https://parermine.regione.emilia-romagna.it//issues/23441) Modifiche in Riepilogo versamenti
- [#21810](https://parermine.regione.emilia-romagna.it//issues/21810) Definire report trasformazione

## 4.5.1

### Bugfix: 9
- [#23128](https://parermine.regione.emilia-romagna.it//issues/23128) Pulsante Set trasformazione in errore: non deve aprire una nuova sessione
- [#23121](https://parermine.regione.emilia-romagna.it//issues/23121) Errore nel recupero errori di trasformazione su oggetti in warning
- [#23081](https://parermine.regione.emilia-romagna.it//issues/23081) Errore passando al Tipo oggetto e al Versatore dal link nel dettaglio trasformazione.
- [#23055](https://parermine.regione.emilia-romagna.it//issues/23055) Oggetti in stato TRASFORMAZIONE_NON_ATTIVA non vengono più trasformati
- [#23011](https://parermine.regione.emilia-romagna.it//issues/23011) Errore critico in modifica Nome del Tipo oggetto
- [#22992](https://parermine.regione.emilia-romagna.it//issues/22992) Dettaglio trasformazione: nome file errato versioni precedenti
- [#22698](https://parermine.regione.emilia-romagna.it//issues/22698) Dettaglio associazione ente convenzionato: il formato della data non è corretto
- [#21846](https://parermine.regione.emilia-romagna.it//issues/21846) Path su Versatori non si aggiornano in base al valore del parametro DS_PREFISSO_PATH
- [#18721](https://parermine.regione.emilia-romagna.it//issues/18721) Errore di inserimento nuova trasformazione non catturato

### Novità: 4
- [#23080](https://parermine.regione.emilia-romagna.it//issues/23080) Errore trasformazione se l'istanza di kettle-server non viene trovata
- [#23057](https://parermine.regione.emilia-romagna.it//issues/23057) Report trasformazione: aggiungere informazioni
- [#23012](https://parermine.regione.emilia-romagna.it//issues/23012) Revisione monitoraggio per sezione trasformazioni
- [#22953](https://parermine.regione.emilia-romagna.it//issues/22953) [PING] Aggiunta codice fiscale nel messaggio di errore per l'utente in caso di login non autorizzato via SPID

## 4.5.0 (23-09-2020)

### Bugfix: 5
- [#23019](https://parermine.regione.emilia-romagna.it//issues/23019) Errore non gestito in esegui trasformazione
- [#23014](https://parermine.regione.emilia-romagna.it//issues/23014) Visualizzazione fornitore esterno
- [#22991](https://parermine.regione.emilia-romagna.it//issues/22991) Modifica Tipo oggetto in dettaglio oggetto non viene recepito dal sistema
- [#22333](https://parermine.regione.emilia-romagna.it//issues/22333) Errore passando al tipo oggetto dal link nel dettaglio trasformazione.
- [#22321](https://parermine.regione.emilia-romagna.it//issues/22321) Parametri su tipo oggetto non vengono aggiornati in visualizzazione

### Novità: 2
- [#22636](https://parermine.regione.emilia-romagna.it//issues/22636) Gestione delle trasformazioni in warning
- [#22312](https://parermine.regione.emilia-romagna.it//issues/22312) Modifiche a supporto della parallelizzazione di più istanze di Kettle Server

## 4.4.5 (17-08-2020)

### Novità: 1
- [#22783](https://parermine.regione.emilia-romagna.it//issues/22783) Recepimento aggiornamenti librerie Secondo quadrimestre 2020 - SACERPING

## 4.4.4 (21-07-2020)

### Bugfix: 1
- [#22594](https://parermine.regione.emilia-romagna.it//issues/22594) Strumenti urbanistici: verifica solo se sono presenti documenti non verificati

### Novità: 4
- [#22602](https://parermine.regione.emilia-romagna.it//issues/22602) Strumenti urbanistici: modifiche all'interfaccia web
- [#22601](https://parermine.regione.emilia-romagna.it//issues/22601) Modifiche alla pagina Scegli versatore
- [#22317](https://parermine.regione.emilia-romagna.it//issues/22317) Integrazione con SPID
- [#20768](https://parermine.regione.emilia-romagna.it//issues/20768) Versatori: calcolare l'ambito territoriale

### SUE: 1
- [#22305](https://parermine.regione.emilia-romagna.it//issues/22305) Rilascio PING in preproduzione e produzione  4.4.4 (versione Miglioramenti strumenti urbanistici + Ambito territoriale)

## 4.4.3 (07-07-2020)

### Bugfix: 1
- [#22518](https://parermine.regione.emilia-romagna.it//issues/22518) Strumenti urbanistici: versamento solo se tutti i documenti obbligatori sono presenti

## 4.4.2 (03-07-2020)

### Bugfix: 1
- [#22509](https://parermine.regione.emilia-romagna.it//issues/22509) Adeguamento a framework 4.1.5

## 4.4.1 (02-07-2020)

### Bugfix: 3
- [#22425](https://parermine.regione.emilia-romagna.it//issues/22425) Strumenti urbanistici: risoluzione problemi su strumenti in stato ERRORE
- [#22424](https://parermine.regione.emilia-romagna.it//issues/22424) Strumenti urbanistici: modifica campo Descrizione
- [#22423](https://parermine.regione.emilia-romagna.it//issues/22423) Strumenti urbanistici: errori su verifica sincrona

## 4.4.0 (24-06-2020)

### Bugfix: 1
- [#22172](https://parermine.regione.emilia-romagna.it//issues/22172) Strumenti urbanistici: consentire la modifica in stato ERRORE

### Novità: 3
- [#22310](https://parermine.regione.emilia-romagna.it//issues/22310) Strumenti urbanistici: aggiunta campo Descrizione
- [#22260](https://parermine.regione.emilia-romagna.it//issues/22260) Modifica modalità di verifica file zip per PUG 
- [#21807](https://parermine.regione.emilia-romagna.it//issues/21807) Strumenti urbanistici: rendere sincrona la verifica dei file zip

## 4.3.1 (15-06-2020)

### Bugfix: 5
- [#22245](https://parermine.regione.emilia-romagna.it//issues/22245) Errore nell'importazione del Versatore
- [#22244](https://parermine.regione.emilia-romagna.it//issues/22244) Cessazione versatore: consentita anche con accordo valido
- [#22173](https://parermine.regione.emilia-romagna.it//issues/22173) Strumenti urbanistici: impedire il salvataggio dello Strumento in assenza di dati obbligatori
- [#22148](https://parermine.regione.emilia-romagna.it//issues/22148) La creazione di nuovi set di parametri nella trasformazione non viene riportato automaticamente nel "versatore per cui si generano oggetti"
- [#22023](https://parermine.regione.emilia-romagna.it//issues/22023) Pulire i dati inseriti nel report dai caratteri non ammessi in un xml

### Novità: 2
- [#22149](https://parermine.regione.emilia-romagna.it//issues/22149) Interventi sul Dettaglio trasformazione
- [#22071](https://parermine.regione.emilia-romagna.it//issues/22071) Aggiornamento dei parametri standard della trasformazione

### SUE: 1
- [#21937](https://parermine.regione.emilia-romagna.it//issues/21937) Rilascio PING in preproduzione e produzione (versione Cancellazione logica)

## 4.3.0 (28-05-2020)

### Bugfix: 6
- [#22222](https://parermine.regione.emilia-romagna.it//issues/22222) Lista versamenti falliti: presenti risultati relativi a versatori cessati
- [#22130](https://parermine.regione.emilia-romagna.it//issues/22130) Strumenti urbanistici: download diretto del rapporto di versamento
- [#22104](https://parermine.regione.emilia-romagna.it//issues/22104) Il paramtero XF_REPORT_ID non deve comparire tra i parametri possibili
- [#22098](https://parermine.regione.emilia-romagna.it//issues/22098) Valorizzazione campo unione sugli strumenti urbanistici
- [#21872](https://parermine.regione.emilia-romagna.it//issues/21872) Cancellazione versatore: errore critico
- [#21830](https://parermine.regione.emilia-romagna.it//issues/21830) Caratteri unicode all'interno dell'indice MM

### Novità: 2
- [#22136](https://parermine.regione.emilia-romagna.it//issues/22136) Strumenti urbanistici: consentire sempre la presenza di cartelle nei file zip
- [#21444](https://parermine.regione.emilia-romagna.it//issues/21444) Interventi per consentire la cancellazione logica

## 4.2.0 (12-05-2020)

### Bugfix: 3
- [#21806](https://parermine.regione.emilia-romagna.it//issues/21806) Strumenti urbanistici: il pulsante Versamento non viene visualizzato
- [#21780](https://parermine.regione.emilia-romagna.it//issues/21780) Correzione information leakage - evolutiva
- [#21622](https://parermine.regione.emilia-romagna.it//issues/21622) Data di associazione versatore-ente  non corretta

### Novità: 4
- [#21977](https://parermine.regione.emilia-romagna.it//issues/21977) Modifiche interfaccia Versamento Strumenti urbanistici
- [#21794](https://parermine.regione.emilia-romagna.it//issues/21794) Aggiungere la gestione del parametro della trasformazione XF_REPORT_ID
- [#21430](https://parermine.regione.emilia-romagna.it//issues/21430) Dettaglio trasformazione: inserire link al Tipo oggetto e al Versatore
- [#18943](https://parermine.regione.emilia-romagna.it//issues/18943) Gestire lo user sacer come un parametro

### SUE: 1
- [#21900](https://parermine.regione.emilia-romagna.it//issues/21900) Rilascio PING in preproduzione e produzione (versione 4.2.0)

## 4.1.2 (16-04-2020)

### Bugfix: 1
- [#21772](https://parermine.regione.emilia-romagna.it//issues/21772) Comunicazione Kettle-server - Ping 

## 4.1.1 (14-04-2020)

### Novità: 1
- [#15914](https://parermine.regione.emilia-romagna.it//issues/15914) Accettare in automatico gli studi in warning, tenendo traccia del warning

## 4.1.0 (11-03-2020)

### Bugfix: 3
- [#21483](https://parermine.regione.emilia-romagna.it//issues/21483) Errore nel salvataggio di una nuova versione di trasformazione
- [#21253](https://parermine.regione.emilia-romagna.it//issues/21253) Strumenti urbanistici: pulsanti non gestibili da ruoli
- [#19354](https://parermine.regione.emilia-romagna.it//issues/19354) Amministrazione versatori e amministrazione ambienti: il conservatore deve poter operare come gestore

### Novità: 8
- [#21256](https://parermine.regione.emilia-romagna.it//issues/21256) Aggiornamento framework JQUERY
- [#21212](https://parermine.regione.emilia-romagna.it//issues/21212) Modifiche interfaccia Strumenti urbanistici
- [#21195](https://parermine.regione.emilia-romagna.it//issues/21195) Introdurre stato PREPARAZIONE_OGG_IN_CORSO
- [#21176](https://parermine.regione.emilia-romagna.it//issues/21176) Scelta versatore: prevedere due combo come su Sacer
- [#20856](https://parermine.regione.emilia-romagna.it//issues/20856) Modifica denominazione stati per abbandono XFormer
- [#20830](https://parermine.regione.emilia-romagna.it//issues/20830) Modifica gestione path ftp sul Versatore
- [#15178](https://parermine.regione.emilia-romagna.it//issues/15178) Modifica Tipo oggetto in dettaglio oggetto per gli oggetti da trasformare
- [#13978](https://parermine.regione.emilia-romagna.it//issues/13978) Settare in errore pacchetti in fase di trasformazione

## 4.0.3 (10-02-2020)

### Bugfix: 3
- [#21201](https://parermine.regione.emilia-romagna.it//issues/21201) Data strumento urbanistico: utilizzare la data dell'atto
- [#21200](https://parermine.regione.emilia-romagna.it//issues/21200) Mancata visualizzazione dettaglio oggetto da monitoraggio
- [#20064](https://parermine.regione.emilia-romagna.it//issues/20064) Correzione information leakage

### Novità: 1
- [#21193](https://parermine.regione.emilia-romagna.it//issues/21193) Modifiche interfaccia Strumenti urbanistici

## 4.0.2 (06-02-2020)

### Bugfix: 5
- [#21126](https://parermine.regione.emilia-romagna.it//issues/21126) Non vengono visualizzati Strumenti urbanistici in stato VERSATO
- [#21116](https://parermine.regione.emilia-romagna.it//issues/21116) Gestione priorità su servizio versamento oggetto
- [#21097](https://parermine.regione.emilia-romagna.it//issues/21097) Problemi di navigazione in amministrazione ping
- [#21096](https://parermine.regione.emilia-romagna.it//issues/21096) Aggiornamento stato Strumento Urbanistico in seguito ad annullamento Oggetto versato
- [#21093](https://parermine.regione.emilia-romagna.it//issues/21093) Aggiunta flag su tipo oggetto per governare sua visualizzazione nelle varie pagine di versamento oggetto

### Novità: 1
- [#21090](https://parermine.regione.emilia-romagna.it//issues/21090) Modifiche messaggi di errore e interfaccia Strumenti urbanistici

## 4.0.1 (28-01-2020)

### Bugfix: 12
- [#21061](https://parermine.regione.emilia-romagna.it//issues/21061)  Rimozione username in URL nella pagina di cambio password
- [#21005](https://parermine.regione.emilia-romagna.it//issues/21005) Visalizzati campi sbagliati dopo errore in versamento oggetto trasformato
- [#20989](https://parermine.regione.emilia-romagna.it//issues/20989) Servizio Invio oggetto: mancata gestione priorità nulla
- [#20988](https://parermine.regione.emilia-romagna.it//issues/20988) Priorità sul tipo oggetto non obbligatoria
- [#20890](https://parermine.regione.emilia-romagna.it//issues/20890) errore al salvataggio di nuova versione trasformazione
- [#20871](https://parermine.regione.emilia-romagna.it//issues/20871) Inserimento priorità su oggetto
- [#20787](https://parermine.regione.emilia-romagna.it//issues/20787) Salvataggio Versatore: gestire la mancata valorizzazione dei parametri del path FTP
- [#20786](https://parermine.regione.emilia-romagna.it//issues/20786) Gestione dei parametri nulli e del caricamento del file csv
- [#20779](https://parermine.regione.emilia-romagna.it//issues/20779) Controllo date collegamenti anche sulle fasi successive
- [#20761](https://parermine.regione.emilia-romagna.it//issues/20761) Problemi di navigazione in amministrazione ping
- [#19707](https://parermine.regione.emilia-romagna.it//issues/19707) Esporta versatore non funziona da scorciatoia
- [#19145](https://parermine.regione.emilia-romagna.it//issues/19145) Correzioni a pagina Dettaglio versatore

### Novità: 7
- [#20990](https://parermine.regione.emilia-romagna.it//issues/20990) Versamento oggetto da trasformare: eliminare combo Priorità trasformazione
- [#20983](https://parermine.regione.emilia-romagna.it//issues/20983) Aggiunta flag su tipo oggetto per governare sua visualizzazione nelle varie pagine di versamento oggetto
- [#20819](https://parermine.regione.emilia-romagna.it//issues/20819) aggiornamento stato Strumento Urbanistico in seguito ad annullamento Oggetto versato
- [#20739](https://parermine.regione.emilia-romagna.it//issues/20739) downalod files Strumenti Urbanistici
- [#20736](https://parermine.regione.emilia-romagna.it//issues/20736) Gestione parametri del Versatore su pagina separata
- [#19724](https://parermine.regione.emilia-romagna.it//issues/19724) Informazione sulla trasformazione utilizzata in Dettaglio versamento
- [#17892](https://parermine.regione.emilia-romagna.it//issues/17892) Visualizzazione del report della trasformazione

### SUE: 1
- [#20857](https://parermine.regione.emilia-romagna.it//issues/20857) Eliminazione XFormer

## 4.0.0

### EVO: 1
- [#17409](https://parermine.regione.emilia-romagna.it//issues/17409) Trasferimento su Ping delle funzioni gestite in XFormer

### Bugfix: 1
- [#20325](https://parermine.regione.emilia-romagna.it//issues/20325) Parametri sul tipo oggetto: problema di visualizzazione

### Novità: 2
- [#19723](https://parermine.regione.emilia-romagna.it//issues/19723) Versamento oggetto: non valorizzare la descrizione vuota
- [#19428](https://parermine.regione.emilia-romagna.it//issues/19428) Priorità oggetti da trasformare

## 3.8.0 (10-12-2019)

### EVO: 1
- [#20449](https://parermine.regione.emilia-romagna.it//issues/20449) Sostituzione libreria obsoleta xercesImpl-2.10.0.jar (apache:xerces2_java:2.10.0)

### Novità: 8
- [#20448](https://parermine.regione.emilia-romagna.it//issues/20448) Sostituzione libreria obsoleta xalan-2.7.1.jar (apache:xalan-java:2.7.1)
- [#20446](https://parermine.regione.emilia-romagna.it//issues/20446) Sostituzione libreria obsoleta poi-3.9.jar (apache:poi:3.9)
- [#20445](https://parermine.regione.emilia-romagna.it//issues/20445) Sostituzione libreria obsoleta jackson-databind-2.4.1.jar (fasterxml:jackson-databind:2.4.1) 
- [#20443](https://parermine.regione.emilia-romagna.it//issues/20443) Sostituzione libreria obsoleta commons-fileupload-1.3.jar (apache:commons_fileupload:1.3)
- [#20442](https://parermine.regione.emilia-romagna.it//issues/20442) Sostituzione libreria obsoleta commons-collections-3.2.1.jar (apache:commons_collections:3.2.1)
- [#20441](https://parermine.regione.emilia-romagna.it//issues/20441) Sostituzione libreria obsoleta bcprov-jdk15-1.46.jar (org.bouncycastle:bcprov-jdk15:1.46)
- [#20328](https://parermine.regione.emilia-romagna.it//issues/20328) Caricamento parametri trasformazione da csv
- [#20268](https://parermine.regione.emilia-romagna.it//issues/20268) Strumenti urbanistici: aggiungere pagina Dettaglio strumento urbanistico

## 3.7.7

### Bugfix: 1
- [#20437](https://parermine.regione.emilia-romagna.it//issues/20437) Cestino solo per strumenti urbanistici in BOZZA

### Novità: 3
- [#20507](https://parermine.regione.emilia-romagna.it//issues/20507) inserimento date Strumenti Urbanistici
- [#20382](https://parermine.regione.emilia-romagna.it//issues/20382) Controllo date collegamenti
- [#20275](https://parermine.regione.emilia-romagna.it//issues/20275) Modifiche al menu

## 3.7.6

### Bugfix: 1
- [#20175](https://parermine.regione.emilia-romagna.it//issues/20175) Errore salvataggio file con nome troppo lungo

### Novità: 3
- [#20350](https://parermine.regione.emilia-romagna.it//issues/20350) sostituzione metodo calcolo ambito territoriale PUG
- [#20266](https://parermine.regione.emilia-romagna.it//issues/20266) Strumenti urbanistici: modifica alla codifica dell'identificativo dell'oggetto da trasformare
- [#20262](https://parermine.regione.emilia-romagna.it//issues/20262) Strumenti urbanistici: modifiche all'interfaccia e ai controlli

## 3.7.5 (23-10-2019)

### Novità: 1
- [#20042](https://parermine.regione.emilia-romagna.it//issues/20042) Aggiornare entity ORG_V_RIC_ENTE_CONVENZ

## 3.7.4

### Bugfix: 2
- [#20155](https://parermine.regione.emilia-romagna.it//issues/20155) correzione tipologie documentarie Strumenti Urbanistici
- [#20152](https://parermine.regione.emilia-romagna.it//issues/20152) Collegamenti non aggiornati post cambio tipologia Strumento Urbanistico e altre correzioni

## 3.7.3

### Bugfix: 1
- [#20118](https://parermine.regione.emilia-romagna.it//issues/20118) configurazione JOB StrumentiUrbanistici

### Novità: 3
- [#20148](https://parermine.regione.emilia-romagna.it//issues/20148) aggiornamento set tipologia ud
- [#20126](https://parermine.regione.emilia-romagna.it//issues/20126) valorizzazione campo UNIONE di ambito territoriale per versatori di tipologia COMUNE
- [#20046](https://parermine.regione.emilia-romagna.it//issues/20046) normalizzazione parametri versatore

## 3.7.2

### Bugfix: 2
- [#20050](https://parermine.regione.emilia-romagna.it//issues/20050) errore job InvioStrumentoUrbanistico
- [#20035](https://parermine.regione.emilia-romagna.it//issues/20035) revisione Strumenti Urbanistici

### Novità: 2
- [#20062](https://parermine.regione.emilia-romagna.it//issues/20062) Strumenti urbanistici: modifiche all'interfaccia di versamento
- [#16243](https://parermine.regione.emilia-romagna.it//issues/16243) Abilitazione token (csrf) su tag libray di spago-lite utilizzate per generare apposite form (HTTP POST) in PING

## 3.7.0

### EVO: 1
- [#17165](https://parermine.regione.emilia-romagna.it//issues/17165) Versamenti e nuova ricerca per PUG (piani urbanistici)

### Novità: 2
- [#19652](https://parermine.regione.emilia-romagna.it//issues/19652) Estensione attività versamento PUG
- [#19272](https://parermine.regione.emilia-romagna.it//issues/19272) Modifica censimento xsd tipo oggetto PING

## 3.6.2

### Bugfix: 1
- [#19991](https://parermine.regione.emilia-romagna.it//issues/19991) job recuperoErroreVersamento, flag da recuperare

### Novità: 1
- [#19993](https://parermine.regione.emilia-romagna.it//issues/19993) Modifica campi in Lista oggetti

## 3.6.1

### Novità: 1
- [#19788](https://parermine.regione.emilia-romagna.it//issues/19788) Rimozione MEV 15373

## 3.6.0

### Bugfix: 11
- [#19309](https://parermine.regione.emilia-romagna.it//issues/19309) Parametri trasformazione pari a "null" e visualizzazione pagina Dettaglio set parametri versatore
- [#19244](https://parermine.regione.emilia-romagna.it//issues/19244) Popolamento combo ente convenzionato in fase di creazione versatore
- [#18566](https://parermine.regione.emilia-romagna.it//issues/18566) Dettaglio oggetto derivante da versamenti falliti: errore critico
- [#18065](https://parermine.regione.emilia-romagna.it//issues/18065) errore modifica dettaglio oggetto
- [#17889](https://parermine.regione.emilia-romagna.it//issues/17889) Editing verificato in Dettaglio versamento per oggetto di tipo DA_TRASFORMARE
- [#16113](https://parermine.regione.emilia-romagna.it//issues/16113) Dettaglio trasformazione: il campo Trasformazione utilizzata non viene aggiornato
- [#15916](https://parermine.regione.emilia-romagna.it//issues/15916) Copiatura XML vers a SACER quando creo sessione
- [#15910](https://parermine.regione.emilia-romagna.it//issues/15910) Set verificato per sessione in timeout
- [#15768](https://parermine.regione.emilia-romagna.it//issues/15768) Caricamento dettaglio oggetto derivante da versamenti falliti in errore
- [#15435](https://parermine.regione.emilia-romagna.it//issues/15435) Versamento oggetto ZIP CON XML SACER: errore inatteso
- [#14678](https://parermine.regione.emilia-romagna.it//issues/14678) Visualizzazione dato "Size"

### Novità: 3
- [#17495](https://parermine.regione.emilia-romagna.it//issues/17495) Impronta calcolata sugli oggetti conservati: passare a SHA-256
- [#16114](https://parermine.regione.emilia-romagna.it//issues/16114) Possibilità di impostare dati sul singolo oggetto e singola sessione
- [#15373](https://parermine.regione.emilia-romagna.it//issues/15373) Versamento oggetto: consentire il versamento di oggetti da trasformare con più punti nel nome file

## 3.5.2

### Bugfix: 1
- [#19255](https://parermine.regione.emilia-romagna.it//issues/19255) Controllo dimensione lista modalityInStudy e lista SOPClass

## 3.5.1 (20-06-2019)

### Bugfix: 1
- [#18935](https://parermine.regione.emilia-romagna.it//issues/18935) Problemi nella gestione del versatore

### Novità: 1
- [#18922](https://parermine.regione.emilia-romagna.it//issues/18922) Valorizzazione combo ambiente in creazione versatore

## 3.5.0 (31-05-2019)

### Novità: 2
- [#18627](https://parermine.regione.emilia-romagna.it//issues/18627) Java 8 e re-factor parer-pom / framework / site
- [#18193](https://parermine.regione.emilia-romagna.it//issues/18193) Adeguamento di Ping a Multi conservatore

## 3.4.5 (29-05-2019)

### Novità: 2
- [#18738](https://parermine.regione.emilia-romagna.it//issues/18738) Ordinamento oggetti da mettere in coda versamento
- [#18661](https://parermine.regione.emilia-romagna.it//issues/18661) Parametro per numero massimo DICOM da versare a Sacer

## 3.4.4 (14-03-2019)

### Bugfix: 3
- [#18004](https://parermine.regione.emilia-romagna.it//issues/18004) UnmarshalException durante l'annullamento di unità doc versate in Sacer
- [#17975](https://parermine.regione.emilia-romagna.it//issues/17975) Parametri trasformazione pari a "null" e visualizzazione pagina Dettaglio set parametri versatore
- [#16013](https://parermine.regione.emilia-romagna.it//issues/16013) Risoluzione errore su recupero studi in warning

## 3.4.2 (01-03-2019)

### Bugfix: 1
- [#17901](https://parermine.regione.emilia-romagna.it//issues/17901) Problema formattazione date dopo rimozione Castor

## 3.4.1 (26-02-2019)

### EVO: 1
- [#17083](https://parermine.regione.emilia-romagna.it//issues/17083) Gestione parametri per multiconservatore PING

### Bugfix: 1
- [#16108](https://parermine.regione.emilia-romagna.it//issues/16108) Rimuovere riferimenti a Castor

## 3.3.11.1

### Novità: 1
- [#17596](https://parermine.regione.emilia-romagna.it//issues/17596) Eliminazione jks dal sorgente dell'applicazione.

## 3.3.11

### Bugfix: 1
- [#16876](https://parermine.regione.emilia-romagna.it//issues/16876) Caratteri non ammessi nei parametri della trasformazione sul tipo oggetto

## 3.3.10 (28-11-2018)

### Bugfix: 1
- [#16750](https://parermine.regione.emilia-romagna.it//issues/16750) Monitoraggio Coda Morta : gestione messaggio "generico" 

## 3.3.9 (26-11-2018)

### Bugfix: 1
- [#15911](https://parermine.regione.emilia-romagna.it//issues/15911) Errore su "Indietro" dopo "modifica" in "dettaglio sessione fallita"

### Novità: 2
- [#16487](https://parermine.regione.emilia-romagna.it//issues/16487) Eliminazione dell'Ambiente dall'identificativo dei Versatori
- [#16160](https://parermine.regione.emilia-romagna.it//issues/16160) Malfuzionamento navigazione dal dettaglio versatore

## 3.3.8 (21-09-2018)

### Bugfix: 5
- [#16082](https://parermine.regione.emilia-romagna.it//issues/16082) Aumento tempo di deploy - timeout
- [#16033](https://parermine.regione.emilia-romagna.it//issues/16033) Contromisure alla vulnerabilità session termination
- [#15954](https://parermine.regione.emilia-romagna.it//issues/15954) Versamento oggetto: malfunzionamento del pulsante Nuovo versamento
- [#15930](https://parermine.regione.emilia-romagna.it//issues/15930) Duplicazione versatore: le PK devono essere non monotoniche
- [#15633](https://parermine.regione.emilia-romagna.it//issues/15633) Impostazione flag HttpOnly e secure sul cookie JSESSIONID

### Novità: 1
- [#16002](https://parermine.regione.emilia-romagna.it//issues/16002) Link al versatore nella colonna di sinistra

## 3.3.7 (31-07-2018)

### Novità: 1
- [#15723](https://parermine.regione.emilia-romagna.it//issues/15723) Ricerca Stato versamenti: modifiche ai filtri di ricerca

## 3.3.6 (27-07-2018)

### Bugfix: 2
- [#15680](https://parermine.regione.emilia-romagna.it//issues/15680) Impaginazione stato versamenti
- [#15546](https://parermine.regione.emilia-romagna.it//issues/15546) rischio di IP spoofing legato all'utilizzo dell'header http X-Forwarded-For.

### Novità: 1
- [#15700](https://parermine.regione.emilia-romagna.it//issues/15700) Modifiche pagina Dettaglio stati versamenti

## 3.3.5 (27-06-2018)

### Bugfix: 1
- [#15507](https://parermine.regione.emilia-romagna.it//issues/15507) Elabora risposta annullamento

## 3.3.4

### Bugfix: 1
- [#15470](https://parermine.regione.emilia-romagna.it//issues/15470) Consumer coda versamento: eliminazione cartelle oggetti generati da trasformazione

## 3.3.3 (22-06-2018)

### Bugfix: 1
- [#15446](https://parermine.regione.emilia-romagna.it//issues/15446) Versamento UD - errore se tipo versamento da trasformare = false

## 3.3.2 (10-05-2018)

### Bugfix: 6
- [#15042](https://parermine.regione.emilia-romagna.it//issues/15042) Gestione Encoding in Ping
- [#14483](https://parermine.regione.emilia-romagna.it//issues/14483) Gestire Esito non conforme di annullamento da Sacer
- [#14220](https://parermine.regione.emilia-romagna.it//issues/14220) Chiusura risorse per evitare memory leak nell'accodamento del messaggio
- [#13060](https://parermine.regione.emilia-romagna.it//issues/13060) Chiamata al servizio di richiesta annullamento di un oggetto contenente solo UD in stato VERSATA_ERR
- [#13039](https://parermine.regione.emilia-romagna.it//issues/13039) Filtro delle ud di un oggetto
- [#11511](https://parermine.regione.emilia-romagna.it//issues/11511) Controlli su Versamento oggetto

### Novità: 11
- [#14663](https://parermine.regione.emilia-romagna.it//issues/14663) gestione delle unità documentarie già annullate in sacer
- [#14653](https://parermine.regione.emilia-romagna.it//issues/14653) Gestione oggetto DA TRASFORMARE in stato Annullato
- [#14652](https://parermine.regione.emilia-romagna.it//issues/14652) Gestione annullamento oggetto ZIP_XML_SACER senza annullare i versamenti
- [#14585](https://parermine.regione.emilia-romagna.it//issues/14585) Recupero errori di trasformazione: prevedere nuovi stati
- [#14561](https://parermine.regione.emilia-romagna.it//issues/14561) Estensione annullamento oggetti in errore
- [#14555](https://parermine.regione.emilia-romagna.it//issues/14555) Prepara XML: nei controlli di coerenza considerare la presenza di caratteri non ammessi
- [#14347](https://parermine.regione.emilia-romagna.it//issues/14347) Tipo oggetto: impedire la modifica del nome 
- [#14100](https://parermine.regione.emilia-romagna.it//issues/14100) Oggetti in errore: non prevedere la cancellazione
- [#14041](https://parermine.regione.emilia-romagna.it//issues/14041) Gestione codice versatore per cui si generano oggetti - servizio di versamento
- [#12942](https://parermine.regione.emilia-romagna.it//issues/12942) Calcolo e controllo dei path in configurazione del versatore
- [#12941](https://parermine.regione.emilia-romagna.it//issues/12941) WS Invio oggetto: estendere controlli semantici in riferimento al Tipo oggetto/Tipo SIP versato

## 3.2.6

### Novità: 1
- [#15271](https://parermine.regione.emilia-romagna.it//issues/15271) Versamento oggetto: consentire il versamento di file in altri formati oltre lo ZIP e controllo lunghezza codice

## 3.2.5 (09-05-2018)

### Novità: 1
- [#15058](https://parermine.regione.emilia-romagna.it//issues/15058) Parametro per gestire numero di ud massimo in coda

## 3.2.4 (11-04-2018)

### Bugfix: 1
- [#14809](https://parermine.regione.emilia-romagna.it//issues/14809) WS invio oggetto: non viene calcolato il versatore per cui generare oggetti

## 3.2.3 (08-03-2018)

### Bugfix: 1
- [#14482](https://parermine.regione.emilia-romagna.it//issues/14482) Richiesta di annullamento: aumentare dimensione del codice che identifica la richiesta

## 3.2.2

### Bugfix: 1
- [#14467](https://parermine.regione.emilia-romagna.it//issues/14467) Inibizione dei pulsanti nella pagina di gestione job

## 3.2.1 (23-01-2018)

### Bugfix: 2
- [#13965](https://parermine.regione.emilia-romagna.it//issues/13965) Duplicazione/Importazione Versatori - mancata valorizzazione di alcuni campi
- [#13960](https://parermine.regione.emilia-romagna.it//issues/13960) Versamento unità documentarie: modificare nome pagina

### Novità: 2
- [#13754](https://parermine.regione.emilia-romagna.it//issues/13754) Lista oggetti derivanti da versamenti falliti: modifiche alla pagina
- [#13040](https://parermine.regione.emilia-romagna.it//issues/13040) Gestione codice versatore per cui si generano oggetti

## 3.2.0 (08-01-2018)

### EVO: 1
- [#11003](https://parermine.regione.emilia-romagna.it//issues/11003) Duplicazione versatori

### Bugfix: 1
- [#13750](https://parermine.regione.emilia-romagna.it//issues/13750) Dettaglio tipo oggetto: errori nella modifica dei dati

### Novità: 3
- [#13111](https://parermine.regione.emilia-romagna.it//issues/13111) Disattivazione automatica degli utenti automa in caso di ripetuti fallimenti del login
- [#13041](https://parermine.regione.emilia-romagna.it//issues/13041) Gestione di pagine di versamento diverse per utenti esterni
- [#12940](https://parermine.regione.emilia-romagna.it//issues/12940) Tipo oggetto: rinominare Tipo Versamento File in Tipo SIP 

## 3.1.1 (08-11-2017)

### Bugfix: 1
- [#13038](https://parermine.regione.emilia-romagna.it//issues/13038) Errore nell'associazione di un xsd a un tipo file

### Novità: 2
- [#13452](https://parermine.regione.emilia-romagna.it//issues/13452) Adeguamento release 3.1.0 di Ping a Framework 2.0.0
- [#13353](https://parermine.regione.emilia-romagna.it//issues/13353) Schedulazioni Job: aggiungere i job di Xformer

## 3.0.5.2 (01-12-2017)

### Bugfix: 1
- [#13580](https://parermine.regione.emilia-romagna.it//issues/13580) Per gli studi inviati, una volta lanciata la ricerca e identificato lo studio, la pagina dei dettagli è vuota

## 3.0.5.1

### Bugfix: 1
- [#13171](https://parermine.regione.emilia-romagna.it//issues/13171) Risoluzione problema errato utilizzo di tmp in fase di upload file

## 3.0.2 (08-03-2017)

### Bugfix: 1
- [#10947](https://parermine.regione.emilia-romagna.it//issues/10947) Visibilità versatori

## 3.0.1 (20-02-2017)

### Bugfix: 1
- [#10818](https://parermine.regione.emilia-romagna.it//issues/10818) Fix su riepilogo versamenti per utilizzare il filtro sul tipo oggetto.
