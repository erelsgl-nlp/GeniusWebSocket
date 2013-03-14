-- nlp_interface
---- nlp_instance_biutee
---- nlp_instance_eng
---- nlp_instance_biutee_eng

interface nlp_interface = {
	oper
		PhraseType: Type;
		mkPhrase: Str -> {s: Str};
	
		mkNumber: Str -> {s: Str};
		mkNoun: Str -> {s: Str};
		mkAdjective: Str -> {s: Str};
		mkAny: Str -> {s: Str};
}
