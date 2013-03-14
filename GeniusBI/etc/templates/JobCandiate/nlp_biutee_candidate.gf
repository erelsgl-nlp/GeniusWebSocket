-- Create the BIUTEE templates for the candidate, without values (for calculation).
-- Example: "I can agree to a { adjective } promotion track"
concrete nlp_biutee_candidate of nlp_abs = nlp_incomplete_candidate 
  ** nlp_incomplete with (nlp_interface = nlp_instance_biutee);
