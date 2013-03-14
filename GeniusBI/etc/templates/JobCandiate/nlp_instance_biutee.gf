instance nlp_instance_biutee of nlp_interface = {
	oper
		PhraseType: Type = {s: Str};
		mkPhrase: Str -> {s: Str} = \x -> {s = x};

		mkNumber: Str -> {s: Str} = \x -> {s = "{"++"number"++"}"};
		mkNoun: Str -> {s: Str} = \x -> {s = "{"++"noun"++"}"};
		mkAdjective: Str -> {s: Str} = \x -> {s = "{"++"adjective"++"}"};
		mkAny: Str -> {s: Str} = \x -> {s = "{"++"any"++"}"};
}
-- p "I can agree on 20 % pension"
