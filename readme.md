# IF~Dose Android

## Configuration

### Fichier IF-DOSE-Android/app/src/main/res/values/strings.xml

Modifier les paramètres :

```
<string name="medecin_email" translatable="false">email@medecin</string>
<string name="host_adr" translatable="false">http://test-gi.ump.ma</string>
<string name="host_port" translatable="false">80</string>
```
Pour le cas du site http://test-gi.ump.ma/gcalpha il faut ajouter '/gcalpha' 
avant les liens de l'API 
```
<string name="urlCategories" translatable="false">/gcalpha/api/categories</string>
<string name="urlLogin" translatable="false">/gcalpha/api/users/pass=</string>
<string name="urlAlim" translatable="false">/gcalpha/api/aliments/id=</string>
<string name="urlAlims" translatable="false">/gcalpha/api/aliments/category_id=</string>
<string name="urlPost" translatable="false">/gcalpha/api/aliment/name=</string>
<string name="urlPostRapport" translatable="false">/gcalpha/api/rapport/id=</string>
<string name="urlGeneratePDF" translatable="false">/gcalpha/api/sendReport</string>
```

Modifier le champ app_mode par : 
  - dev : mode developpent ou vous pouvez activer le cloud debugging dans les parametres de l'application.
  - prod : le champs d'activation de debuging et inactive. 
NB : il faut redemarrer l'app apres chaque changement.
```
<string name="app_mode" translatable="false">dev</string>
```

Modifier le champ bugfender_app_key avec celui de vous
```
<string name="bugfender_app_key" translatable="false">Mv3uSQeVUjcKBwkwr1uk7cvrfxaTrY9a</string>
```
### Signature de l'application
#### Depuis android studio

- Click sur le menu :  Build > Generate Signed APK
- Selectioner le fichier IF-DOSE-Android/deploy/ifdose.jks  
(vous pouvez créer un nouveau keystore avec des nouveau mot de pass )
- Mettre dans les champs :
    - mot de pass du keystore => 'IFDose'
    - alias => 'ifdose'
    - mot de pass du keystore => 'IFDose'
- Apres cliquer sur suivant et cochez les deux versions de signature et clickez finish.
- L'application sera généré sous le chemin IF-DOSE-Android/app sous le nom 'app-release.apk'
 
### Installation de l'application:

- activer l'option Paramètres > Ecran de vérouillage/Sécurité > Sources unconnues.
- installer l'apk

