-- nlp_incomplete
---- nlp_incomplete_candidate
------ nlp_eng_candidate       (nlp_interface = nlp_instance_eng)
------ nlp_biutee_candidate    (nlp_interface = nlp_instance_biutee)
---- nlp_incomplete_employer
------ nlp_eng_employer        (nlp_interface = nlp_instance_eng)
------ nlp_biutee_employer     (nlp_interface = nlp_instance_biutee)

-- nlp_incomplete_genius
------ nlp_eng_genius       (nlp_interface = nlp_instance_eng)
------ nlp_biutee_genius    (nlp_interface = nlp_instance_biutee)

-- Test by:
-- gt -cat=Salary | lin
-- gt -cat=Demand | lin

incomplete concrete nlp_incomplete of nlp_abs = open nlp_interface in {
  flags coding = latin1;
  
  lincat Action = PhraseType ;
  lin Action_Agree x = mkPhrase x.s ;
  lin Action_Demand x = mkPhrase x.s ;
  lin Action_Other x = mkPhrase x.s ;
  lin Action_PartialAgree x = mkPhrase x.s ;
  lin Action_Insist x = mkPhrase x.s ;
  lin Action_Question x = mkPhrase x.s ;
  lin Action_Quit x = mkPhrase x.s ;
  lin Action_Reject x = mkPhrase x.s ;
  lin Action_Append x = mkPhrase x.s ;
  
  lincat Agree = PhraseType ;
  lin Agree_General = mkPhrase "I accept your offer";

  lincat Append = PhraseType ;
  lin Append_General = mkPhrase "In addition to what I offered before";
  
  lincat Demand = PhraseType ;
  lin Demand_Leased_Car_No_Agreement = mkPhrase "Let's discuss the car issue later";
  lin Demand_Pension_Fund_No_Agreement = mkPhrase "Let's discuss the pension issue later";
  lin Demand_Promotion_Possibilities_No_Agreement = mkPhrase "Let's discuss the promotion possibilities later";
  
  lincat Disagree = PhraseType ;
  lin DisagreeGeneral = mkPhrase "I do not agree with you";

  lincat Happiness = PhraseType ;
  lin Happiness_Excellent = mkPhrase "Excellent";
  lin Happiness_Good = mkPhrase "Good";
  lin Happiness_Great = mkPhrase "Great";
  lin Happiness_I_am_happy_that_you_agree = mkPhrase "I am happy that you agree";
  
  lincat Float = PhraseType ;
  lincat Int = PhraseType ;

  lincat Issue = PhraseType ;
  lin Issue_job_description = mkNoun "job description";
  lin Issue_leased_car = mkNoun "leased car";
  lin Issue_pension = mkNoun "pension";
  lin Issue_promotion_track = mkNoun "promotion track";
  lin Issue_salary = mkNoun "salary";
  lin Issue_working_hours = mkNoun "working hours";
  
  lincat JobDescription = PhraseType ;
  lin JobDescription_Programmer = mkNoun "Programmer";
  lin JobDescription_Project_Manager = mkNoun "Project Manager";
  lin JobDescription_QA = mkNoun "QA";
  lin JobDescription_Team_Manager = mkNoun "Team Manager";
  lin JobDescription_String x = mkNoun x.s;
  
  lincat Misunderstanding = PhraseType ;
  lin Misunderstanding_General = mkPhrase "Sorry, I didn't understand you";
  lin Misunderstanding_Issue x = mkPhrase (x.s ++ "is not one of the issues in our discussion");
  lin Misunderstanding_Value x y = mkPhrase (x.s ++ "is not one of the valid values for" ++ y.s);
  
  lincat Other = PhraseType ;
  lin Other_I_am_waiting = mkPhrase "I am waiting";
  lin Other_time_is_passing = mkPhrase "Time is passing";
  lin Other_String x = mkAny x.s;
  lin Other_hi = mkPhrase "hi";
  lin Other_my_name_is_String x = mkPhrase ("my name is" ++ (mkAny x.s).s);
  
  lincat PartialAgree = PhraseType ;
  lin PartialAgree_General = mkPhrase "I partially accept your offer";
  lin PartialAgree_Issue x = mkPhrase ("I accept your" ++ x.s ++ "offer");
  lincat Insist = PhraseType ;
  lin Insist_General = mkPhrase "I insist on my previous offer";
  lin Insist_Issue x = mkPhrase ("I insist on my previous" ++ x.s ++ "offer");
 
  lincat Pension = PhraseType ;
  lin Pension_0 = mkNumber "0";
  lin Pension_10 = mkNumber "10";
  lin Pension_20 = mkNumber "20";
  lin Pension_Int x = mkNumber x.s;
  
  lincat PromotionTrack = PhraseType ;
  lin PromotionTrack_fast = mkAdjective "fast";
  lin PromotionTrack_slow = mkAdjective "slow";

  lincat Question = PhraseType ;
  lin Question_Agreement = mkPhrase "do we agree";
  lin Question_Final = mkPhrase "Is there anything else we should discuss";

  lincat Quit = PhraseType ;
  lin QuitGeneral = mkPhrase "I must leave this negotiation without an agreement";
  lincat Reject = PhraseType ;
  lin Reject_General = mkPhrase "I cannot accept your offer";
  lin Reject_IssueCount x = mkPhrase ("I already accepted your conditions in" ++ x.s ++ "issues, I expect that you compromise on other issues");

  lincat Salary = PhraseType ;
  lin Salary_12000 = mkNumber "12,000";
  lin Salary_20000 = mkNumber "20,000";
  lin Salary_7000 =  mkNumber "7,000";
  lin Salary_Int x = mkNumber x.s;

  lincat String = PhraseType ;

  lincat WorkingHours = PhraseType ;
  lin WorkingHours_10 = mkNumber "10";
  lin WorkingHours_8 = mkNumber "8";
  lin WorkingHours_9 = mkNumber "9";
  lin WorkingHours_Float x = mkNumber x.s;
 
  lincat YouAgree = PhraseType ;
}
