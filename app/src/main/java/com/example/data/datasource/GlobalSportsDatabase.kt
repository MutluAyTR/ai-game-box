package com.example.data.datasource

/**
 * Authentic Mackolik & Nesine Comprehensive Global Sports Database
 * Covers all sports, leagues, lower tiers, stadiums, TV broadcasts, and players.
 */
object GlobalSportsDatabase {

  // --- FUTBOL TÜRKİYE ---
  val superLigTeams = listOf(
    "Galatasaray", "Fenerbahçe", "Beşiktaş", "Trabzonspor", "Başakşehir",
    "Samsunspor", "Eyüpspor", "Kasımpaşa", "Göztepe", "Sivasspor",
    "Antalyaspor", "Alanyaspor", "Çaykur Rizespor", "Konyaspor", "Gaziantep FK",
    "Kayserispor", "Bodrum FK", "Hatayspor", "Adana Demirspor"
  )

  val tff1LigTeams = listOf(
    "Kocaelispor", "Sakaryaspor", "Amed SK", "Gençlerbirliği", "Bandırmaspor",
    "Iğdır FK", "Çorum FK", "MKE Ankaragücü", "İstanbulspor", "Pendikspor",
    "Fatih Karagümrük", "Ümraniyespor", "Boluspor", "Erzurumspor FK", "Manisa FK",
    "Şanlıurfaspor", "Adanaspor", "Keçiörengücü", "Esenler Erokspor", "Yeni Malatyaspor"
  )

  val tff2LigKirmiziTeams = listOf(
    "Bucaspor 1928", "Vanspor FK", "Çimentaş Elazığspor", "Menemen FK", "Motolux 68 Aksarayspor",
    "Serik Belediyespor", "Karacabey Bld", "Beyoğlu Yeni Çarşı", "Ankara Demirspor", "Arnavutköy Bld",
    "Merkür Jet Erbaaspor", "Karaman FK", "Nazillispor", "Giresunspor", "Diyarbekirspor",
    "Belediye Derincespor", "Somaspor", "Turkish Oil Yeni Mersin İY"
  )

  val tff2LigBeyazTeams = listOf(
    "Sarıyer", "Batman Petrolspor", "GMG Kastamonuspor", "Altınordu", "İskenderunspor",
    "Bulut Yeşil İnşaat Adana 01 FK", "Ankaraspor", "Anagold 24Erzincanspor", "Fethiyespor",
    "1461 Trabzon FK", "Kırklarelispor", "İnegölspor", "Isparta 32 Spor", "Kepezspor",
    "Karaköprü Belediyespor", "Altay", "Hes İlaç Afyonspor", "Sincan Bld Ankaraspor"
  )

  val tff3LigTeams = listOf(
    "Bursaspor", "Düzcespor", "Karşıyaka", "Eskişehirspor", "Orduspor 1967",
    "Kuşadasıspor", "Balıkesirspor", "Silivrispor", "Alanya 1221 FK", "Muşspor",
    "Tokat Bld Plevnespor", "Artvin Hopaspor", "Kırşehir FSK", "Bornova 1877", "Anadolu Üniversitesi",
    "Ayvalıkgücü Bld", "Edirnespor", "Karabük İdmanyurdu", "Pazarspor", "Yozgat Bozokspor",
    "52 Orduspor FK", "Küçükçekmece Sinop", "Aliağa Futbol A.Ş.", "Çorluspor 1947", "Bayburt Özel İdare",
    "Efeler 09 SFK", "Sebat Gençlikspor", "Ergene Velimeşe", "Kestel Çilekspor", "Fatsa Belediyespor"
  )

  // --- FUTBOL AVRUPA & DÜNYA ---
  val premierLeagueTeams = listOf(
    "Manchester City", "Arsenal", "Liverpool", "Chelsea", "Tottenham",
    "Manchester United", "Aston Villa", "Newcastle United", "Brighton", "West Ham United",
    "Fulham", "Brentford", "Bournemouth", "Everton", "Wolverhampton",
    "Crystal Palace", "Nottingham Forest", "Leicester City", "Ipswich Town", "Southampton"
  )

  val championshipTeams = listOf(
    "Leeds United", "Sunderland", "Sheffield United", "Burnley", "West Bromwich Albion",
    "Watford", "Norwich City", "Middlesbrough", "Coventry City", "Luton Town",
    "Blackburn Rovers", "Millwall", "Swansea City", "Bristol City", "Stoke City",
    "Derby County", "Preston North End", "Sheffield Wednesday", "Hull City", "Oxford United",
    "Plymouth Argyle", "Queens Park Rangers", "Portsmouth", "Cardiff City"
  )

  val leagueOneTeams = listOf(
    "Wycombe Wanderers", "Birmingham City", "Wrexham", "Huddersfield Town", "Charlton Athletic",
    "Reading", "Bolton Wanderers", "Barnsley", "Lincoln City", "Peterborough United",
    "Blackpool", "Rotherham United", "Mansfield Town", "Leyton Orient", "Exeter City",
    "Stevenage", "Wigan Athletic", "Bristol Rovers", "Northampton Town", "Burton Albion"
  )

  val laLigaTeams = listOf(
    "Real Madrid", "Barcelona", "Atletico Madrid", "Athletic Bilbao", "Real Sociedad",
    "Villarreal", "Real Betis", "Sevilla", "Girona", "Osasuna",
    "Celta Vigo", "Mallorca", "Rayo Vallecano", "Valencia", "Las Palmas",
    "Espanyol", "Deportivo Alaves", "Getafe", "Leganes", "Real Valladolid"
  )

  val segundaTeams = listOf(
    "Racing Santander", "Sporting Gijon", "Real Zaragoza", "Real Oviedo", "Granada",
    "Levante", "Elche", "Eibar", "Malaga", "Almeria",
    "Mirandes", "Castellon", "Burgos", "Huesca", "Cadiz",
    "Albacete", "Eldense", "Cordoba", "Deportivo La Coruna", "Racing Ferrol"
  )

  val serieATeams = listOf(
    "Inter", "Juventus", "AC Milan", "Napoli", "Atalanta",
    "AS Roma", "Lazio", "Fiorentina", "Bologna", "Torino",
    "Udinese", "Genoa", "Parma", "Como", "Cagliari",
    "Empoli", "Hellas Verona", "Lecce", "Venezia", "Monza"
  )

  val serieBTeams = listOf(
    "Pisa", "Sassuolo", "Spezia", "Cesena", "Cremonese",
    "Brescia", "Juve Stabia", "Palermo", "Sampdoria", "Bari",
    "Reggiana", "Catanzaro", "Mantova", "Sudtirol", "Carrarese",
    "Salernitana", "Modena", "Cittadella", "Cosenza", "Frosinone"
  )

  val bundesligaTeams = listOf(
    "Bayern München", "Bayer Leverkusen", "Borussia Dortmund", "RB Leipzig", "Eintracht Frankfurt",
    "VfB Stuttgart", "SC Freiburg", "TSG Hoffenheim", "Werder Bremen", "Borussia M'gladbach",
    "FC Augsburg", "VfL Wolfsburg", "Mainz 05", "Union Berlin", "FC St. Pauli",
    "1. FC Heidenheim", "VfL Bochum", "Holstein Kiel"
  )

  val bundesliga2Teams = listOf(
    "Fortuna Düsseldorf", "Hannover 96", "Paderborn", "Hamburger SV", "Karlsruher SC",
    "1. FC Magdeburg", "SV Elversberg", "1. FC Köln", "1. FC Kaiserslautern", "1. FC Nürnberg",
    "Hertha BSC", "Schalke 04", "SV Darmstadt 98", "Greuther Fürth", "Preussen Münster",
    "Eintracht Braunschweig", "SSV Ulm 1846", "SSV Jahn Regensburg"
  )

  val ligue1Teams = listOf(
    "Paris Saint-Germain", "Monaco", "Marseille", "Lille", "Lyon",
    "Lens", "Nice", "Reims", "Rennes", "Strasbourg",
    "Brest", "Nantes", "Auxerre", "Angers", "Saint-Etienne",
    "Toulouse", "Le Havre", "Montpellier"
  )

  val eredivisieTeams = listOf(
    "PSV Eindhoven", "Ajax", "Feyenoord", "FC Utrecht", "FC Twente",
    "AZ Alkmaar", "Go Ahead Eagles", "Fortuna Sittard", "NAC Breda", "Willem II",
    "NEC Nijmegen", "SC Heerenveen", "FC Groningen", "Heracles Almelo", "PEC Zwolle",
    "Sparta Rotterdam", "Almere City", "RKC Waalwijk"
  )

  val ligaPortugalTeams = listOf(
    "Sporting CP", "FC Porto", "Benfica", "Santa Clara", "SC Braga",
    "Vitoria de Guimaraes", "Famalicao", "Moreirense", "Casa Pia", "Gil Vicente",
    "Rio Ave", "Arouca", "Estoril Praia", "Boavista", "AVS Futebol SAD",
    "Nacional", "Estrela da Amadora", "SC Farense"
  )

  val belgiumProTeams = listOf(
    "KRC Genk", "Club Brugge", "Royal Antwerp", "RSC Anderlecht", "KAA Gent",
    "KV Mechelen", "Union Saint-Gilloise", "FCV Dender EH", "KVC Westerlo", "Standard Liege",
    "Sporting Charleroi", "Sint-Truidense VV", "KV Kortrijk", "Cercle Brugge", "OH Leuven", "Beerschot VA"
  )

  val scotlandPremiershipTeams = listOf(
    "Celtic", "Aberdeen", "Rangers", "Dundee United", "Motherwell",
    "St. Mirren", "Dundee FC", "Kilmarnock", "Ross County", "Heart of Midlothian",
    "St. Johnstone", "Hibernian"
  )

  val brazilSerieATeams = listOf(
    "Botafogo", "Palmeiras", "Fortaleza", "Flamengo", "Sao Paulo",
    "Internacional", "Bahia", "Cruzeiro", "Vasco da Gama", "Atletico Mineiro",
    "Corinthians", "Gremio", "Criciúma", "Fluminense", "Vitoria",
    "Athletico Paranaense", "Juventude", "Red Bull Bragantino", "Cuiaba", "Atletico Goianiense"
  )

  val argentinaPrimeraTeams = listOf(
    "Velez Sarsfield", "Huracan", "Talleres de Cordoba", "Union de Santa Fe", "Atletico Tucuman",
    "Racing Club", "River Plate", "Boca Juniors", "Instituto de Cordoba", "Godoy Cruz",
    "Platense", "Deportivo Riestra", "Estudiantes de La Plata", "Lanus", "Belgrano",
    "Independiente", "San Lorenzo", "Newell's Old Boys", "Rosario Central", "Defensa y Justicia"
  )

  val saudiProTeams = listOf(
    "Al Hilal", "Al Ittihad", "Al Nassr", "Al Shabab", "Al Qadsiah",
    "Al Ahli", "Al Riyadh", "Al Ettifaq", "Al Taawoun", "Al Raed",
    "Al Orobah", "Al Khaleej", "Al Wehda", "Damac", "Al Feiha",
    "Al Kholood", "Al Okhdood", "Al Fateh"
  )

  val mlsTeams = listOf(
    "Inter Miami CF", "Los Angeles Galaxy", "Columbus Crew", "Los Angeles FC", "FC Cincinnati",
    "Real Salt Lake", "Orlando City SC", "Seattle Sounders", "Houston Dynamo", "Charlotte FC",
    "New York Red Bulls", "New York City FC", "Minnesota United", "Portland Timbers", "Vancouver Whitecaps",
    "Colorado Rapids", "Austin FC", "FC Dallas", "Atlanta United", "Philadelphia Union"
  )

  val jLeagueTeams = listOf(
    "Sanfrecce Hiroshima", "Vissel Kobe", "Machida Zelvia", "Kashima Antlers", "Gamba Osaka",
    "Tokyo Verdy", "Cerezo Osaka", "FC Tokyo", "Kawasaki Frontale", "Nagoya Grampus",
    "Urawa Red Diamonds", "Avispa Fukuoka", "Kyoto Sanga", "Albirex Niigata", "Kashiwa Reysol",
    "Shonan Bellmare", "Jubilo Iwata", "Consadole Sapporo", "Sagan Tosu", "Yokohama F. Marinos"
  )

  val aLeagueTeams = listOf(
    "Melbourne City", "Sydney FC", "Central Coast Mariners", "Melbourne Victory", "Macarthur FC",
    "Wellington Phoenix", "Western Sydney Wanderers", "Adelaide United", "Brisbane Roar", "Newcastle Jets",
    "Perth Glory", "Auckland FC"
  )

  // --- BASKETBOL ---
  val nbaTeams = listOf(
    "Los Angeles Lakers", "Boston Celtics", "Golden State Warriors", "Milwaukee Bucks", "Denver Nuggets",
    "Dallas Mavericks", "New York Knicks", "Miami Heat", "Phoenix Suns", "Philadelphia 76ers",
    "LA Clippers", "Minnesota Timberwolves", "Oklahoma City Thunder", "Indiana Pacers", "Cleveland Cavaliers",
    "Orlando Magic", "Sacramento Kings", "New Orleans Pelicans", "Memphis Grizzlies", "Houston Rockets",
    "Atlanta Hawks", "Chicago Bulls", "Brooklyn Nets", "Toronto Raptors", "San Antonio Spurs",
    "Charlotte Hornets", "Portland Trail Blazers", "Detroit Pistons", "Utah Jazz", "Washington Wizards"
  )

  val euroLeagueTeams = listOf(
    "Fenerbahçe Beko", "Anadolu Efes", "Real Madrid", "Panathinaikos", "Olympiacos",
    "FC Barcelona", "AS Monaco", "Maccabi Tel Aviv", "Baskonia", "Virtus Bologna",
    "Partizan", "Crvena Zvezda", "Olimpia Milano", "Zalgiris Kaunas", "Bayern Munich Basketball",
    "Paris Basketball", "LDLC ASVEL", "ALBA Berlin"
  )

  val bslTeams = listOf(
    "Fenerbahçe Beko", "Anadolu Efes", "Beşiktaş Fibabanka", "Galatasaray Ekmas", "Pınar Karşıyaka",
    "Türk Telekom", "TOFAŞ", "Bursaspor", "Bahçeşehir Koleji", "Manisa Basket",
    "Petkim Spor", "Büyükçekmece", "Merkezefendi Bld", "Darüşşafaka Lassa", "Mersin Spor", "Yalovaspor"
  )

  val tblTeams = listOf(
    "Esenler Erokspor Basket", "Trabzonspor Basket", "Gaziantep Basketbol", "Çayırova Bld", "Kapaklı Spor",
    "OGM Ormanspor", "TED Ankara Kolejliler", "Bornova Bld Karşıyaka", "Ankaragücü Basket", "Fenerbahçe Koleji"
  )

  val acbTeams = listOf(
    "Real Madrid Baloncesto", "Barça Basket", "Unicaja Malaga", "Valencia Basket", "Baskonia Vitoria",
    "Gran Canaria", "Lenovo Tenerife", "Joventut Badalona", "UCAM Murcia", "Baxi Manresa",
    "Casademont Zaragoza", "Surne Bilbao", "Rio Breogan", "MoraBanc Andorra", "Basquet Girona"
  )

  // --- MOTOR SPORLARI ---
  val motoGpRacers = listOf(
    "Francesco Bagnaia (Ducati)", "Jorge Martin (Pramac)", "Marc Marquez (Gresini)",
    "Enea Bastianini (Ducati)", "Pedro Acosta (Tech3)", "Brad Binder (KTM)",
    "Maverick Vinales (Aprilia)", "Aleix Espargaro (Aprilia)", "Marco Bezzecchi (VR46)",
    "Fabio Di Giannantonio (VR46)", "Alex Marquez (Gresini)", "Franco Morbidelli (Pramac)",
    "Fabio Quartararo (Yamaha)", "Jack Miller (KTM)", "Miguel Oliveira (Trackhouse)",
    "Johann Zarco (LCR)", "Joan Mir (Honda)", "Alex Rins (Yamaha)", "Raul Fernandez (Trackhouse)", "Luca Marini (Honda)"
  )

  val f1Drivers = listOf(
    "Max Verstappen (Red Bull)", "Lando Norris (McLaren)", "Charles Leclerc (Ferrari)",
    "Oscar Piastri (McLaren)", "Carlos Sainz (Ferrari)", "Lewis Hamilton (Mercedes)",
    "George Russell (Mercedes)", "Sergio Perez (Red Bull)", "Fernando Alonso (Aston Martin)",
    "Nico Hülkenberg (Haas)", "Yuki Tsunoda (RB)", "Lance Stroll (Aston Martin)",
    "Alexander Albon (Williams)", "Pierre Gasly (Alpine)", "Esteban Ocon (Alpine)",
    "Kevin Magnussen (Haas)", "Liam Lawson (RB)", "Franco Colapinto (Williams)", "Valtteri Bottas (Kick Sauber)", "Zhou Guanyu (Kick Sauber)"
  )

  val wrcRallyDrivers = listOf(
    "Thierry Neuville (Hyundai)", "Ott Tänak (Hyundai)", "Sébastien Ogier (Toyota)",
    "Elfyn Evans (Toyota)", "Kalle Rovanperä (Toyota)", "Adrien Fourmaux (M-Sport Ford)",
    "Takamoto Katsuta (Toyota)", "Grégoire Munster (M-Sport Ford)", "Andreas Mikkelsen (Hyundai)",
    "Dani Sordo (Hyundai)", "Sami Pajari (Toyota)", "Oliver Solberg (Skoda)",
    "Yohan Rossel (Citroën)", "Nikolay Gryazin (Citroën)", "Gus Greensmith (Skoda)", "Kajetan Kajetanowicz (Skoda)"
  )

  // --- TENİS ---
  val tennisPlayers = listOf(
    "Jannik Sinner", "Carlos Alcaraz", "Novak Djokovic", "Alexander Zverev",
    "Daniil Medvedev", "Stefanos Tsitsipas", "Andrey Rublev", "Casper Ruud",
    "Taylor Fritz", "Hubert Hurkacz", "Grigor Dimitrov", "Holger Rune",
    "Alex de Minaur", "Tommy Paul", "Ben Shelton", "Frances Tiafoe",
    "Aryna Sabalenka", "Iga Swiatek", "Coco Gauff", "Elena Rybakina",
    "Jessica Pegula", "Jasmine Paolini", "Zheng Qinwen", "Emma Navarro",
    "Paula Badosa", "Danielle Collins", "Mirra Andreeva", "Donna Vekic",
    "Ons Jabeur", "Maria Sakkari", "Beatriz Haddad Maia", "Daria Kasatkina"
  )

  // --- VOLEYBOL ---
  val sultanlarLigiTeams = listOf(
    "Fenerbahçe Medicana (K)", "VakıfBank (K)", "Eczacıbaşı Dynavit (K)", "Galatasaray Daikin (K)",
    "Türk Hava Yolları (K)", "Kuzeyboru (K)", "Muratpaşa Bld (K)", "Aydın B.Ş.B. (K)",
    "Nilüfer Bld (K)", "Sarıyer Bld (K)", "Beşiktaş (K)", "Bahçelievler Bld (K)", "Aras Kargo (K)", "Keçiören Bld (K)"
  )

  val efelerLigiTeams = listOf(
    "Halkbank", "Fenerbahçe Medicana (E)", "Ziraat Bankkart", "Galatasaray HDI Sigorta",
    "Arkas Spor", "Spor Toto Voleybol", "Alanya Bld Spor", "Bursa B.Ş.B.",
    "Cizre Bld", "Akkuş Bld", "Develi Bld", "Altekma", "İstanbul Gençlik Spor", "Kuşgöz İzmir Vinç Akkuş Bld"
  )

  // --- BUZ HOKEYİ ---
  val nhlTeams = listOf(
    "Florida Panthers", "Edmonton Oilers", "New York Rangers", "Dallas Stars", "Carolina Hurricanes",
    "Boston Bruins", "Colorado Avalanche", "Vancouver Canucks", "Vegas Golden Knights", "Toronto Maple Leafs",
    "Tampa Bay Lightning", "Winnipeg Jets", "Los Angeles Kings", "Nashville Predators", "Washington Capitals",
    "New York Islanders", "Detroit Red Wings", "Minnesota Wild", "Pittsburgh Penguins", "St. Louis Blues",
    "Philadelphia Flyers", "Buffalo Sabres", "New Jersey Devils", "Calgary Flames", "Seattle Kraken",
    "Montreal Canadiens", "Ottawa Senators", "Utah Hockey Club", "Anaheim Ducks", "Chicago Blackhawks"
  )

  // --- E-SPOR ---
  val cs2Teams = listOf(
    "Natus Vincere (NaVi)", "Team Vitality", "FaZe Clan", "G2 Esports", "MOUZ",
    "Team Spirit", "Eternal Fire", "Virtus.pro", "Astralis", "HEROIC",
    "Team Liquid", "Complexity", "FURIA", "paiN Gaming", "BIG Clan", "ENCE"
  )

  val lolTeams = listOf(
    "T1", "Gen.G", "Bilibili Gaming (BLG)", "Top Esports (TES)", "Hanwha Life Esports",
    "G2 Esports LoL", "Fnatic", "Dplus KIA", "FlyQuest", "MAD Lions KOI",
    "Weibo Gaming", "Team Liquid LoL", "Cloud9", "FUT Esports LoL", "Papara SuperMassive", "Galakticos"
  )

  // --- HENTBOL & SNOOKER & MLB ---
  val handballTeams = listOf(
    "Barça Handbol", "SC Magdeburg", "Aalborg Håndbold", "Telekom Veszprem", "Paris Saint-Germain Handball",
    "Industria Kielce", "Montpellier Handball", "Pick Szeged", "Füchse Berlin", "SG Flensburg-Handewitt",
    "Beşiktaş Hentbol", "Beykoz Bld Spor", "Spor Toto Hentbol", "Nilüfer Bld Hentbol"
  )

  val snookerPlayers = listOf(
    "Ronnie O'Sullivan", "Judd Trump", "Mark Selby", "Neil Robertson",
    "John Higgins", "Mark Allen", "Shaun Murphy", "Kyren Wilson",
    "Ding Junhui", "Luca Brecel", "Ali Carter", "Gary Wilson"
  )

  val dartsPlayers = listOf(
    "Luke Humphries", "Michael van Gerwen", "Luke Littler", "Michael Smith",
    "Gerwyn Price", "Rob Cross", "Peter Wright", "Nathan Aspinall",
    "Dave Chisnall", "Damon Heta", "Danny Noppert", "Chris Dobey"
  )

  val mlbTeams = listOf(
    "New York Yankees", "Los Angeles Dodgers", "Philadelphia Phillies", "Baltimore Orioles",
    "Cleveland Guardians", "Houston Astros", "Milwaukee Brewers", "San Diego Padres",
    "Atlanta Braves", "New York Mets", "Kansas City Royals", "Detroit Tigers",
    "Boston Red Sox", "Chicago Cubs", "Texas Rangers", "Toronto Blue Jays"
  )

  // --- STADYUMLAR ---
  val stadiumMap = mapOf(
    "Galatasaray" to "Rams Park (52.280)",
    "Fenerbahçe" to "Ülker Stadyumu (50.530)",
    "Beşiktaş" to "Tüpraş Stadyumu (42.590)",
    "Trabzonspor" to "Papara Park (40.782)",
    "Başakşehir" to "Başakşehir Fatih Terim Stadyumu (17.152)",
    "Samsunspor" to "Samsun 19 Mayıs Stadyumu (33.919)",
    "Göztepe" to "Gürsel Aksel Stadyumu (20.040)",
    "Kocaelispor" to "Kocaeli Stadyumu (34.712)",
    "Sakaryaspor" to "Yeni Sakarya Atatürk Stadyumu (28.154)",
    "Amed SK" to "Diyarbakır Stadyumu (33.000)",
    "Bursaspor" to "Yüzüncü Yıl Atatürk Stadyumu (43.361)",
    "Arsenal" to "Emirates Stadium (60.704)",
    "Manchester City" to "Etihad Stadium (53.400)",
    "Liverpool" to "Anfield (61.276)",
    "Chelsea" to "Stamford Bridge (40.341)",
    "Manchester United" to "Old Trafford (74.310)",
    "Real Madrid" to "Santiago Bernabéu (81.044)",
    "Barcelona" to "Estadi Olímpic Lluís Companys (55.926)",
    "Atletico Madrid" to "Riyadh Air Metropolitano (70.460)",
    "Inter" to "San Siro (75.817)",
    "Juventus" to "Allianz Stadium (41.507)",
    "AC Milan" to "San Siro (75.817)",
    "Bayern München" to "Allianz Arena (75.000)",
    "Borussia Dortmund" to "Signal Iduna Park (81.365)",
    "Paris Saint-Germain" to "Parc des Princes (47.929)",
    "Fenerbahçe Beko" to "Ülker Spor ve Etkinlik Salonu (13.059)",
    "Anadolu Efes" to "Sinan Erdem Spor Salonu (16.000)",
    "Galatasaray Ekmas" to "Basketbol Gelişim Merkezi (10.000)",
    "Panathinaikos" to "OAKA Altion (18.500)",
    "Olympiacos" to "Barış ve Dostluk Salonu (12.000)",
    "VakıfBank" to "VakıfBank Spor Sarayı",
    "Eczacıbaşı Dynavit" to "Eczacıbaşı Spor Salonu",
    "Fenerbahçe Medicana" to "Burhan Felek Vestel Voleybol Salonu",
    "Los Angeles Lakers" to "Crypto.com Arena (19.079)",
    "Boston Celtics" to "TD Garden (19.156)"
  )

  // --- YILDIZ OYUNCULAR ---
  val starPlayers = mapOf(
    "Galatasaray" to listOf("Mauro Icardi", "Victor Osimhen", "Barış Alper Yılmaz", "Dries Mertens", "Kerem Demirbay"),
    "Fenerbahçe" to listOf("Edin Džeko", "Youssef En-Nesyri", "Dušan Tadić", "Sebastian Szymański", "İrfan Can Kahveci"),
    "Beşiktaş" to listOf("Ciro Immobile", "Rafa Silva", "Gedson Fernandes", "Ernest Muçi", "Semih Kılıçsoy"),
    "Trabzonspor" to listOf("Simon Banza", "Edin Višća", "Enis Destan", "Ozan Tufan"),
    "Arsenal" to listOf("Bukayo Saka", "Kai Havertz", "Martin Ødegaard", "Declan Rice"),
    "Manchester City" to listOf("Erling Haaland", "Kevin De Bruyne", "Phil Foden", "Bernardo Silva"),
    "Liverpool" to listOf("Mohamed Salah", "Luis Díaz", "Dominik Szoboszlai", "Darwin Núñez"),
    "Real Madrid" to listOf("Kylian Mbappé", "Vinícius Júnior", "Jude Bellingham", "Rodrygo"),
    "Barcelona" to listOf("Robert Lewandowski", "Lamine Yamal", "Raphinha", "Pedri"),
    "Inter" to listOf("Lautaro Martínez", "Marcus Thuram", "Hakan Çalhanoğlu", "Nicolò Barella"),
    "Juventus" to listOf("Dušan Vlahović", "Kenan Yıldız", "Teun Koopmeiners"),
    "Bayern München" to listOf("Harry Kane", "Jamal Musiala", "Leroy Sané", "Michael Olise"),
    "Paris Saint-Germain" to listOf("Bradley Barcola", "Ousmane Dembélé", "Randal Kolo Muani", "Vitinha"),
    "Kocaelispor" to listOf("Markao", "Ryan Mendes", "Oğuz Ceylan", "Pedrinho"),
    "Sakaryaspor" to listOf("Fernando Andrade", "Murat Cem Akpınar", "Cebrail Karayel"),
    "Bursaspor" to listOf("Ahmet İlhan Özek", "Sedat Cengiz", "Muhammet Demir")
  )

  fun getScorerForTeam(teamName: String): String {
    val list = starPlayers[teamName]
    return if (!list.isNullOrEmpty()) list.random() else MackolikComprehensivePlayerDatabase.getScorerForTeam(teamName)
  }
}
