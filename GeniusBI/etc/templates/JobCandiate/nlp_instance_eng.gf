instance nlp_instance_eng of nlp_interface = {
	oper
		PhraseType: Type = {s: Str};
		mkPhrase: Str -> {s: Str} = \x -> {s = x};

		mkNumber: Str -> {s: Str} = \x -> {s = x};
		mkNoun: Str -> {s: Str} = \x -> {s = x};
		mkAdjective: Str -> {s: Str} = \x -> {s = x};
		mkAny: Str -> {s: Str} = \x -> {s = x};
}
-- p "I can agree on 20 % pension"
