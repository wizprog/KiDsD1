KiDsD1

get file|dir		//dok u kesu nema dir rezultata blokira se
get file|summary	//blokira se dok u kesu nema "SUMARRY"-kljuc u hashu, pokrene prebrojavanje u ResultRetriever

get web|url			//da li ima u kesu, ako nema zablokira se i pokrene prebrojavanje
get web|summary		//blokira se dok u kesu nema "SUMARRY"-kljuc u hashu, pokrene prebrojavanje u ResultRetriever

query file|dir		//samo pita kes
query file|summary	//samo pita kes

query web|url		//samo pita kes
query web|summary	//samo pita kes

components/ResultRetrieverThreadPool - NOTFINISHED kljuc za result retriever kada nije zavrsio, koristi se u mainu radi formatiranja ispisa

u queryResult sam pisao kod koji ide u getResult