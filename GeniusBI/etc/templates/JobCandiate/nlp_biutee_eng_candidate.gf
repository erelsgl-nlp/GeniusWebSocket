-- Create the BIUTEE templates for the candidate, with values (for training).
-- Example: "I can agree to a { adjective: fast } promotion track"
concrete nlp_biutee_eng_candidate of nlp_abs = nlp_incomplete_candidate 
  ** nlp_incomplete with (nlp_interface = nlp_instance_biutee_eng);
