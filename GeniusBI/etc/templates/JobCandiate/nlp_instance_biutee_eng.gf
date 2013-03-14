instance nlp_instance_biutee_eng of nlp_interface = {
	oper
		PhraseType: Type = {s: Str};
		mkPhrase: Str -> {s: Str} = \x -> {s = x};

		mkNumber: Str -> {s: Str} = \x -> {s = "{"++"number"+":"++x++"}"};
		mkNoun: Str -> {s: Str} = \x -> {s = "{"++"noun"+":"++x++"}"};
		mkAdjective: Str -> {s: Str} = \x -> {s = "{"++"adjective"+":"++x++"}"};
		mkAny: Str -> {s: Str} = \x -> {s = "{"++"any"+":"++x++"}"};
		
}
-- p "I can agree on 20 % pension"
