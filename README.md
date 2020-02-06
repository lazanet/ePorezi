# ePorezi
Декомпајлирана апликација за eПорезе са подршком за Linux, MacOS, Windows и JDK11

Aпликација [еПорези](http://www.poreskauprava.gov.rs/sr/e-porezi/portal.html) са одличним [grakic-евим модификацијама](https://gist.github.com/grakic/32af1e15ec626cae1e5e17e3c146c486) како би радила на Linux/MacOS-у и додатним преправкама како би радила на новим верзијама Јаве.

# Requirements (Ubuntu 18.04)
* openjdk11 (било који >= 8 би требало да ради)
* [Поштин SafeSign](https://www.ca.posta.rs/preuzimanje_softvera.htm)
* [libgdbm3](https://packages.ubuntu.com/xenial/amd64/libgdbm3/download) (обрисан из стандардних репозиторијума)
* Поштин сертификат (MУП-ов са личних карти ради само на Windows и [од скоро MacOS](https://nikola.milojevic.me/blog/eporezi-macos-mup/) системима, види [овде](http://zverko.rs/2015/01/sta-je-to-elektronski-potpis-kako-se-koristi-i-kako-instalirati-to-cudo/) за више информација и проблематику).
* Ако пријављује да не види картицу, погледати секцију Могући проблеми [овде](http://jfreesteel.devbase.net/program/ubuntu/#accordionP).

Корисно је прочитати grakic-eв gist за [Линукс](https://gist.github.com/grakic/32af1e15ec626cae1e5e17e3c146c486)/[MacOS](https://gist.github.com/grakic/9a850411c3b9294ff0c226e4f914be35), будући да је у њему све лепо описано.

Windows корисници у теорији могу да drop-in замене `ePorezi.jar` из `Program Files` са оним билдованим одавде.

# Build
Неопходно је да имате инсталиран `maven`
```bash
git clone https://github.com/lazanet/ePorezi.git
cd ePorezi
mvn clean package
```

# Run
```bash
cd ePorezi/target
java -jar ePorezi-1.0.jar
```

# Install (Ubuntu 18.04)
```bash
cd ePorezi
sudo mkdir -p /opt/ePorezi
sudo cp target/ePorezi-1.0.jar /opt/ePorezi/ePorezi.jar
sudo cp launcher/* /opt/ePorezi/
sudo ln -s /opt/ePorezi/ePorezi.desktop /usr/share/applications/ePorezi.desktop
```

# License
Будући да је ово неофицијелна апликација, користите је на сопствену одговорност (иако не би требало да буде икаквих проблема јер је сав промењен код само замена API-јева). Изворни код је добијен тривијалним позивом јавног [fernflower декомпајлера](https://the.bytecode.club/fernflower.jar) са јавном апликацијом еПорези, и служи само како би омогућио корисницима овог система од јавног интереса боље услове за рад (а све због некомпетентности оригиналних аутора). 

Сваки PR, било за документовање било за додавање фичра је добродошао. 
TODO листа изгледа отприлике овако:
* ребазирати печеве на еПорези 1.1 верзију
* средити build систем да генеришe `.exe` / `.deb` / `.dmg`
* написати крштено упутство за кориснике MacOS / Windows-a
* декомпајлирати `libnstpkcs11.so` Ghidra-ом и пробати прављење 64-битног билда.
