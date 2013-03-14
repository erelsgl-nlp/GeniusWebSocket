-- Create the BIUTEE templates for the employer, without values (for calculation).
-- Example: "I can agree to a { adjective } promotion track"
concrete nlp_biutee_employer of nlp_abs = nlp_incomplete_employer
  ** nlp_incomplete with (nlp_interface = nlp_instance_biutee);
