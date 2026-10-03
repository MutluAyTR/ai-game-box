package com.example.data.datasource

/**
 * Extended Authentic Global Sports Data
 * Supplies comprehensive real teams, clubs, athletes, and tournaments for lower tiers
 * and global circuits to power 2000+ non-repeating matches on 26.09.2026.
 */
object GlobalExpandedSportsData {

  // --- TÜRKİYE ALT LİGLERİ & AMATÖR ---
  val balTeams = listOf(
    "Çorluspor 1947", "Gebzespor", "İnegöl Kafkasspor", "Gölcükspor", "Yalova Yeşilova",
    "Kapaklıspor", "Tekirdağspor", "Lüleburgazspor", "Edirnespor Gençlik", "Babaeskispor",
    "Kullar 1975 Spor", "Diliskelesispor", "Karamürselspor", "Tayfunspor", "Derincespor A.Ş.",
    "Aliağa FAŞ", "İzmirspor", "Bornova Yeşilovaspor", "Manisa 1965 SK", "Turgutluspor",
    "Sökespor", "Didim Belediyespor", "Muğlaspor", "Yatağanspor", "Kuşadası 1922",
    "Çivril Kıralan Demirspor", "Sarayköyspor", "Burdur MAKÜ Gençlik", "Kumluca Belediyespor", "Manavgat Belediyespor",
    "Eskişehirspor", "Eskişehir Yunusemrespor", "Polatlı Belediyespor", "Sincan Belediyespor", "Gölbaşı Belediyespor",
    "Bartınspor", "Kdz. Ereğli Belediyespor", "Devrek Belediyespor", "Karabük İdmanyurdu", "Boyabat 1868 Spor",
    "Kastamonu Özel İdare", "Çarşambaspor", "Ünye 1957 Spor", "Görelespor", "1926 Bulancakspor",
    "Kars 36 Spor", "Ardahan Serhatspor", "Ağrı 1970 Spor", "Iğdır Arasspor", "Cizrespor",
    "Kurtalanspor", "Mardin 1969 Spor", "Siirt İl Özel İdaresi", "Batman 72 Belediyespor", "Nusaybin Demirspor",
    "Şanlıurfa Büyükşehir", "Adıyaman FK", "Kahta 02 Spor", "Gaziantep Ankasspor", "Kilis Belediyespor",
    "Silifke Belediyespor", "Anamur Belediyespor", "Erdemli Belediyespor", "Reyhanlıspor", "Kırıkhan Spor"
  )

  val tff3LigExpanded = listOf(
    "Karşıyaka", "Bursaspor", "Düzcespor", "Eskişehirspor", "Orduspor 1967",
    "Balıkesirspor", "Silivrispor", "Alanya 1221 FK", "Muşspor", "Tokat Bld Plevne",
    "Artvin Hopaspor", "Kırşehir FSK", "Bornova 1877", "Anadolu Üniversitesi", "Ayvalıkgücü Bld",
    "Edirnespor", "Karabük İdmanyurdu", "Pazarspor", "Yozgat Bozokspor", "52 Orduspor FK",
    "Küçükçekmece Sinop", "Aliağa Futbol", "Çorluspor 1947", "Bayburt Özel İdare", "Efeler 09 SFK",
    "Sebat Gençlikspor", "Ergene Velimeşe", "Kestel Çilekspor", "Fatsa Belediyespor", "Amasyaspor FK",
    "Bulvarspor", "Büyükçekmece Tepecik", "Beyoğlu Yeni Çarşı", "Çatalcaspor", "Erbaaspor",
    "Gümüşhanespor", "Hacettepe 1945", "Karaköprü Bld", "Kelkit Hürriyetspor", "Kırıkkalegücü",
    "Kuşadasıspor", "Malatya Arguvan", "Mardin 1969", "Sapanca Gençlikspor", "Siirt İl Özel İdare",
    "Sivas Dört Eylül", "Sultanbeyli Bld", "Talasgücü Bld", "Tarsus İdman Yurdu", "Tepecikspor"
  )

  val u19ElitTeams = listOf(
    "Galatasaray U19", "Fenerbahçe U19", "Beşiktaş U19", "Trabzonspor U19", "Başakşehir U19",
    "Altınordu U19", "Bursaspor U19", "Konyaspor U19", "Antalyaspor U19", "Kasımpaşa U19",
    "Samsunspor U19", "Göztepe U19", "Sivasspor U19", "Alanyaspor U19", "Gaziantep FK U19",
    "Kayserispor U19", "Gençlerbirliği U19", "Adana Demirspor U19", "İstanbulspor U19", "Ankaragücü U19"
  )

  // --- İNGİLTERE LEAGUE TWO & NON-LEAGUE ---
  val leagueTwoTeams = listOf(
    "Port Vale", "Walsall", "Doncaster Rovers", "Notts County", "Gillingham",
    "Chesterfield", "Crewe Alexandra", "AFC Wimbledon", "Milton Keynes Dons", "Grimsby Town",
    "Salford City", "Bradford City", "Fleetwood Town", "Tranmere Rovers", "Harrogate Town",
    "Newport County", "Cheltenham Town", "Barrow", "Accrington Stanley", "Bromley",
    "Swindon Town", "Carlisle United", "Colchester United", "Morecambe"
  )

  val nationalLeagueTeams = listOf(
    "York City", "Forest Green Rovers", "Barnet", "Rochdale", "Gateshead",
    "Oldham Athletic", "Solihull Moors", "Altrincham", "Halifax Town", "Dagenham & Redbridge",
    "Southend United", "Eastleigh", "Yeovil Town", "Tamworth", "Wealdstone",
    "Sutton United", "Woking", "Hartlepool United", "Aldershot Town", "Braintree Town",
    "Maidenhead United", "Boston United", "AFC Fylde", "Ebbsfleet United"
  )

  val nationalNorthSouthTeams = listOf(
    "Scunthorpe United", "Curzon Ashton", "Chorley", "Kidderminster Harriers", "Brackley Town",
    "Chester", "Hereford", "Buxton", "South Shields", "Spennymoor Town",
    "Torquay United", "Farnborough", "Worthing", "Truro City", "Weston-super-Mare",
    "Chelmsford City", "Slough Town", "Hemel Hempstead", "Boreham Wood", "Tonbridge Angels"
  )

  // --- İSPANYA PRIMERA & SEGUNDA FEDERACIÓN ---
  val primeraRfefTeams = listOf(
    "Cultural Leonesa", "Real Sociedad B", "Barça Atletic", "Andorra", "Gimnastic Tarragona",
    "Ponferradina", "Celta Fortuna", "Arenteiro", "Lugo", "Osasuna B",
    "Betis Deportivo", "Antequera", "Murcia", "Ceuta", "Merida AD",
    "Ibiza", "Alcoyano", "Algeciras", "Hercules CF", "Fuenlabrada",
    "Villarreal B", "Castilla (Real Madrid B)", "Atletico Madrid B", "Sevilla Atletico", "Bilbao Athletic"
  )

  // --- ALMANYA 3. LIGA & REGIONALLIGA ---
  val liga3GermanTeams = listOf(
    "SV Sandhausen", "Dynamo Dresden", "Arminia Bielefeld", "Energie Cottbus", "1. FC Saarbrücken",
    "SV Wehen Wiesbaden", "FC Erzgebirge Aue", "FC Ingolstadt 04", "Viktoria Köln", "Borussia Dortmund II",
    "Alemannia Aachen", "Rot-Weiss Essen", "SC Verl", "VfL Osnabrück", "Hansa Rostock",
    "SpVgg Unterhaching", "Hannover 96 II", "Waldhof Mannheim", "VfB Stuttgart II", "1. FC Schweinfurt"
  )

  val regionalligaTeams = listOf(
    "Kickers Offenbach", "FSV Frankfurt", "Stuttgarter Kickers", "TSV Steinbach", "FC 08 Homburg",
    "Rot-Weiß Oberhausen", "MSV Duisburg", "Sportfreunde Lotte", "SV Rödinghausen", "1. FC Bocholt",
    "Würzburger Kickers", "SpVgg Bayreuth", "FV Illertissen", "SV Wacker Burghausen", "FC Bayern München II",
    "1. FC Lokomotive Leipzig", "Hallescher FC", "FSV Zwickau", "BFC Dynamo", "VSG Altglienicke"
  )

  // --- İTALYA SERIE C ---
  val serieCTeams = listOf(
    "Padova", "Vicenza", "Feralpisalo", "Renate", "Trento",
    "Atalanta U23", "Lumezzane", "Albinoleffe", "Lecco", "Novara",
    "Pescara", "Ternana", "Virtus Entella", "Torres", "Arezzo",
    "Gubbio", "Campobasso", "Pineto", "Perugia", "Rimini",
    "Benevento", "Audace Cerignola", "Monopoli", "Avellino", "Potenza",
    "Catania", "Picerno", "Trapani", "Crotone", "Giugliano"
  )

  // --- DİĞER AVRUPA LİGLERİ ---
  val eredivisieEersteDivisie = listOf(
    "Excelsior", "Helmond Sport", "FC Den Bosch", "SC Cambuur", "De Graafschap",
    "FC Volendam", "FC Dordrecht", "FC Emmen", "Roda JC", "ADO Den Haag",
    "Telstar", "MVV Maastricht", "FC Eindhoven", "TOP Oss", "VVV-Venlo", "Jong AZ"
  )

  val liga2PortugalTeams = listOf(
    "Penafiel", "Tondela", "Benfica B", "Leixoes", "Chaves",
    "Academico Viseu", "Torreense", "Alverca", "Vizela", "Portimonense",
    "Felgueiras 1932", "Maritimo", "Feirense", "Pacos de Ferreira", "Porto B", "Uniao de Leiria"
  )

  val austriaSwissTeams = listOf(
    "Sturm Graz", "Red Bull Salzburg", "Rapid Wien", "Austria Wien", "LASK Linz",
    "Wolfsberger AC", "FC Basel", "Young Boys", "FC Zürich", "FC Lugano",
    "Servette FC", "FC St. Gallen", "FC Luzern", "Grasshoppers", "FC Sion", "Winterthur"
  )

  val scandinavianTeams = listOf(
    "Malmö FF", "Hammarby", "Djurgården", "AIK Stockholm", "IF Elfsborg",
    "Bodø/Glimt", "Brann", "Rosenborg", "Viking", "Molde",
    "FC København", "FC Midtjylland", "Brøndby IF", "AGF Aarhus", "Nordsjælland", "Silkeborg"
  )

  val easternEuropeTeams = listOf(
    "Lech Poznan", "Legia Warszawa", "Rakow Czestochowa", "Jagiellonia Bialystok", "Slask Wroclaw",
    "Slavia Praha", "Sparta Praha", "Viktoria Plzen", "Banik Ostrava", "Mlada Boleslav",
    "FCSB (Steaua)", "CFR Cluj", "Universitatea Craiova", "Dinamo Bucuresti", "Rapid Bucuresti",
    "Dinamo Zagreb", "Hajduk Split", "Rijeka", "Osijek", "Red Star Belgrade (Crvena Zvezda)",
    "Partizan Belgrade", "Vojvodina", "PAOK", "AEK Athens", "Olympiacos Piraeus", "Panathinaikos FC"
  )

  // --- GÜNEY AMERİKA & MEKSİKA ---
  val southAmericaTeams = listOf(
    "Santos FC", "Sport Recife", "Novorizontino", "Mirassol", "Vila Nova",
    "Ceará", "America Mineiro", "Coritiba", "Operario-PR", "Goias",
    "San Martin de Tucuman", "San Martin de San Juan", "Quilmes", "All Boys", "Nueva Chicago",
    "Atletico Nacional", "Millonarios", "Santa Fe", "Junior Barranquilla", "America de Cali",
    "Colo-Colo", "Universidad de Chile", "Universidad Catolica", "Club America", "Cruz Azul",
    "Toluca", "Tigres UANL", "Monterrey", "Guadalajara (Chivas)", "Pumas UNAM"
  )

  // --- BASKETBOL: NCAA KOLEJ LİGİ & DİĞER LİGLER ---
  val ncaaBasketballColleges = listOf(
    "Duke Blue Devils", "North Carolina Tar Heels", "Kansas Jayhawks", "Kentucky Wildcats", "UConn Huskies",
    "Gonzaga Bulldogs", "Houston Cougars", "Purdue Boilermakers", "Arizona Wildcats", "Baylor Bears",
    "Tennessee Volunteers", "Auburn Tigers", "Alabama Crimson Tide", "Texas Longhorns", "UCLA Bruins",
    "Michigan State Spartans", "Indiana Hoosiers", "Villanova Wildcats", "Marquette Golden Eagles", "Creighton Bluejays",
    "Iowa State Cyclones", "Illinois Fighting Illini", "Wisconsin Badgers", "Ohio State Buckeyes", "Arkansas Razorbacks",
    "Florida Gators", "Virginia Cavaliers", "Texas Tech Red Raiders", "San Diego State Aztecs", "Saint Mary's Gaels",
    "Miami Hurricanes", "Xavier Musketeers", "Memphis Tigers", "Dayton Flyers", "Providence Friars",
    "Clemson Tigers", "Colorado Buffaloes", "Utah Utes", "BYU Cougars", "Cincinnati Bearcats"
  )

  val euroCupBasketballTeams = listOf(
    "Hapoel Tel Aviv", "Valencia Basket", "Gran Canaria", "Bahçeşehir Koleji", "Türk Telekom",
    "Umana Reyer Venezia", "Cedevita Olimpija", "JL Bourg", "ratiopharm Ulm", "Buducnost VOLI",
    "Besiktas Fibabanka", "Joventut Badalona", "Trento", "Aris Thessaloniki", "Trefl Sopot",
    "Wolves Vilnius", "Cluj-Napoca", "Hamburg Towers", "Lietkabelis", "Slask Wroclaw Basketball"
  )

  // --- TENİS CHALLENGER & ITF DÜNYA TURU OYUNCULARI ---
  val tennisChallengerItfPlayers = listOf(
    "Borna Coric", "Fabio Fognini", "Richard Gasquet", "Diego Schwartzman", "Stan Wawrinka",
    "Harold Mayot", "Luca Van Assche", "Pierre-Hugues Herbert", "Hugo Gaston", "Benjamin Bonzi",
    "Thiago Monteiro", "Camilo Ugo Carabelli", "Facundo Bagnis", "Román Andrés Burruchaga", "Federico Coria",
    "Duje Ajdukovic", "Lukas Klein", "Zsombor Piros", "Otto Virtanen", "Mikhail Kukushkin",
    "Altuğ Çelikbilek", "Cem İlkel", "Ergi Kırkın", "Yankı Erel", "Koray Kırcı",
    "Mert Naci Türker", "Sarp Ağabigün", "Marsel İlhan", "Taha Bağırsakçı", "Kuzey Çekirge",
    "Zeynep Sönmez", "İpek Öz", "Berfu Cengiz", "Ayla Aksu", "Çağla Büyükakçay",
    "Daria Snigur", "Polina Kudermetova", "Aliona Bolsova", "Simona Waltert", "Celine Naef",
    "Linda Fruhvirtova", "Brenda Fruhvirtova", "Robin Montgomery", "Alex Eala", "Maya Joint",
    "Dominic Stricker", "Arthur Cazaux", "Leandro Riedi", "Mark Lajal", "Gauthier Onclin"
  )

  // --- MASA TENİSİ (SETKA CUP, TT CUP, LİGA PRO) ---
  val tableTennisRoster = listOf(
    "Ivan Pandur", "Dmytro Baistriuchenko", "Serhii Kuprykov", "Yuriy Schepanskiy", "Oleg Prihodko",
    "Vitalii Sydorenko", "Valerii Merzlikin", "Roman Korovai", "Petro Chubenko", "Mykhailo Mosunov",
    "Vasyl Zaplatynskyi", "Oleksandr Fedorchenko", "Pavlo Lukyanov", "Vladyslav Klymenko", "Ihor Samokysh",
    "Radek Rose", "Petr Libovicky", "Tomas Andrle", "Jan Manhal", "Michal Regner",
    "Martin Kowalik", "Jakub Vrabec", "Ales Hruska", "David Jicha", "Kamil Klement",
    "Stanislav Pinc", "Milan Fisera", "Vaclav Hruska", "Milan Longin", "Jiri Svec",
    "Grzegorz Poliniewicz", "Mateusz Misiak", "Adrian Eliasz", "Marcin Marchlewski", "Krzysztof Kapik",
    "Piotr Chodorski", "Damian Wojdyla", "Michal Skorski", "Jakub Glanowski", "Robert Floras",
    "Igor Minchenkov", "Mikhail Reznikov", "Dmitry Tikhnenko", "Alexey Innazarov", "Denis Sayanov",
    "Sergey Ogay", "Evgeny Voronkov", "Kirill Fadeev", "Vladimir Nemashkalo", "Alexander Serebrennikov"
  )

  // --- BUZ HOKEYİ: AHL & AVRUPA ---
  val ahlEuropeanHockeyTeams = listOf(
    "Hershey Bears", "Coachella Valley Firebirds", "Milwaukee Admirals", "Grand Rapids Griffins", "Cleveland Monsters",
    "Rochester Americans", "Syracuse Crunch", "Providence Bruins", "Wilkes-Barre/Scranton", "Hartford Wolf Pack",
    "Toronto Marlies", "Belleville Senators", "Laval Rocket", "Manitoba Moose", "Calgary Wranglers", "Abbotsford Canucks",
    "Färjestad BK", "Skellefteå AIK", "Frölunda HC", "Luleå Hockey", "Rögle BK", "Växjö Lakers",
    "Tappara Tampere", "Ilves Tampere", "Kärpät Oulu", "HIFK Helsinki", "TPS Turku", "Pelicans Lahti",
    "Eisbären Berlin", "Red Bull München", "Adler Mannheim", "Kölner Haie", "ERC Ingolstadt", "Pinguins Bremerhaven",
    "ZSC Lions", "HC Davos", "EV Zug", "SC Bern", "Fribourg-Gottéron", "Genève-Servette HC"
  )

  // --- VOLEYBOL: AVRUPA & KADINLAR 1. LİGİ ---
  val extendedVolleyballTeams = listOf(
    "Sir Susa Vim Perugia", "Itas Trentino", "Cucine Lube Civitanova", "Gas Sales Bluenergy Piacenza", "Allianz Milano",
    "Vero Volley Monza", "Valsa Group Modena", "Rana Verona", "Pallavolo Padova", "Cisterna Volley",
    "Jastrzebski Wegiel", "Aluron CMC Warta Zawiercie", "Projekt Warszawa", "Asseco Resovia Rzeszow", "ZAKSA Kedzierzyn-Kozle",
    "Karayolları (K)", "İBA Kimya TED Ankara (K)", "Göztepe Voleybol (K)", "Edremit Bld Altınoluk (K)", "Bolu Belediyespor (K)",
    "PTT Spor (K)", "İstanbul BBSK Voleybol (K)", "Merdan Ali Gür Spor (K)", "Ereğli Belediyespor (K)", "Karşıyaka Medical Park (K)"
  )

  // --- E-SPOR: DOTA 2 & VALORANT & EA SPORTS FC ---
  val extendedEsportsTeams = listOf(
    "Team Liquid Dota", "Gaimin Gladiators", "Team Falcons Dota", "Tundra Esports", "BetBoom Team",
    "Xtreme Gaming", "PSG Quest", "Cloud9 Dota", "Aurora Gaming", "HEROIC Dota",
    "Sentinels Valorant", "Fnatic Valorant", "Paper Rex", "Leviatán", "Team Heretics Valorant",
    "DRX Valorant", "Edward Gaming (EDG)", "Karmine Corp Valorant", "FUT Esports Valorant", "BBL Esports Valorant",
    "Emre Yılmaz (FUT FC)", "Nicolas99fc", "Vejrgang (RBLZ)", "Ollelito", "Levi de Weerd", "Tekkz (Man City)"
  )

  // --- MOTOR SPORLARI: MOTO2, MOTO3, NASCAR, WRC ---
  val moto2Moto3Riders = listOf(
    "Ai Ogura (MT Helmets)", "Sergio Garcia (MT Helmets)", "Joe Roberts (OnlyFans)", "Alonso Lopez (SpeedUp)",
    "Fermin Aldeguer (SpeedUp)", "Aron Canet (Fantic)", "Celestino Vietti (Ajo)", "Tony Arbolino (Marc VDS)",
    "David Alonso (Aspar Moto3)", "Daniel Holgado (GasGas)", "Collin Veijer (Intact GP)", "Ivan Ortola (MT Helmets)"
  )

  val nascarDrivers = listOf(
    "Kyle Larson (Hendrick)", "Chase Elliott (Hendrick)", "William Byron (Hendrick)", "Denny Hamlin (Gibbs)",
    "Christopher Bell (Gibbs)", "Martin Truex Jr (Gibbs)", "Tyler Reddick (23XI)", "Bubba Wallace (23XI)",
    "Ryan Blaney (Penske)", "Joey Logano (Penske)", "Brad Keselowski (RFK)", "Chris Buescher (RFK)"
  )

  val wrcChileStages = listOf(
    "SS1 Pulperia 1 (19.7 km)", "SS2 Rere 1 (13.3 km)", "SS3 San Rosendo 1 (23.3 km)",
    "SS4 Pulperia 2 (19.7 km)", "SS5 Rere 2 (13.3 km)", "SS6 San Rosendo 2 (23.3 km)",
    "SS7 Pelun 1 (15.6 km)", "SS8 Lota 1 (25.6 km)", "SS9 Maria las Cruces 1 (28.3 km)",
    "SS10 Pelun 2 (15.6 km)", "SS11 Lota 2 (25.6 km)", "SS12 Maria las Cruces 2 (28.3 km)",
    "SS13 Laraquete 1 (18.6 km)", "SS14 Bio Bio 1 (8.7 km)", "SS15 Laraquete 2 (18.6 km)", "SS16 Bio Bio 2 Wolf Power Stage"
  )
}
