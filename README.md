# DPNK Comparator

Aplikace pro porovnání seznamů účastníků kampaně Do práce na kole. Výsledkem porovnání je seznam účastníků, kteří na prvním seznamu jsou a na druhém ne.
Motivací bylo dohledání účastníků, kteří se jeden ročník kampaně účastnili, ale v druhém nikoliv. Podporovány jsou formáty `XLS` a `XLSX`.

## Vyžadováno

* Nainstalovaný [Maven](http://maven.apache.org/).
* Java 7 či vyšší

## Sestavení

`mvn clean install`

## Spuštění

`mvn exec:java -Dexec.mainClass="com.jasnapaka.dpnk.Main" -Dexec.args="soubor1.xlsx soubor2.xlsx"`