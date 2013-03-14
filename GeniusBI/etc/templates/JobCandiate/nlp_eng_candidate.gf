-- Create the English sentences for the candidate.
-- Example: "I can agree to a fast promotion track"
concrete nlp_eng_candidate of nlp_abs = nlp_incomplete_candidate 
  ** nlp_incomplete with (nlp_interface = nlp_instance_eng);
