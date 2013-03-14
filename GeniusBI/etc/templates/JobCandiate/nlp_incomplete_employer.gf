---- nlp_incomplete_employer
------ nlp_eng_employer        (nlp_interface = nlp_instance_eng)
------ nlp_biutee_employer     (nlp_interface = nlp_instance_biutee)

incomplete concrete nlp_incomplete_employer of nlp_abs = nlp_incomplete ** {
  lin Agree_Job_Description x = {s = "I can offer you a" ++ x.s ++ "position"} ;
  lin Agree_Pension_Fund x = {s = "I can agree on" ++ x.s ++ "% pension"};
  lin Agree_Promotion_Possibilities x = {s = "I can agree to a" ++ x.s ++ "promotion track"} ;
  lin Agree_Working_Hours x = {s = "I can agree on a work day of" ++ x.s ++ "hours"} ;
  lin Agree_Leased_Car_With_Leased_Car = {s = "I can give you a company car"} ;
  lin Agree_Salary x = {s = "I can agree to give you" ++ x.s ++ "NIS per month"} ;
  
  lin Demand_Job_Description x = {s = "You can be a"++x.s} ;
  lin Demand_Leased_Car_With_Leased_Car = {s = "I want to give you a company car" | "I demand that you get a company car"};
  lin Demand_Leased_Car_Without_Leased_Car = {s = "I do not offer a company car"};
  lin Demand_Pension_Fund x = {s = "I can give" ++ x.s ++ "% pension"};
  lin Demand_Promotion_Possibilities x = {s = "I can offer you a" ++ x.s ++ "promotion track"};
  lin Demand_Salary x = {s = "I want you to work for" ++ x.s ++ "NIS per month"};
  lin Demand_Working_Hours x = {s = "I want you to work for" ++ x.s ++ "hours a day"};

  lin Question_Initial = {s = "what are your demands"} ;
  lin Question_Issue x = {s = "what are your demands regarding" ++ x.s} ;
  lin Question_Job_Description = {s = "what would you like as your job description"} ;
  lin Question_Leased_Car = {s = "do you demand a leased car"} ;
  lin Question_Promotion_Possibilities = {s = "what promotion track is the best for you"} ;
  lin Question_Salary = {s = "what are your salary demands"} ;
  lin Question_Working_Hours = {s = "how many hours would you like to work each day"} ;

  lin Reject_Job_Description = {s = "we do not need this job right now"} ;
  lin Reject_Leased_Car = {s = "leased car is too expensive for us"} ;
  lin Reject_Pension_Fund = {s = "the pension you ask for is too high for us"} ;
  lin Reject_Promotion_Possibilities = {s = "I do not want to commit to this promotion track"} ;
  lin Reject_Salary = {s = "the salary you ask for is too high"} ;
  lin Reject_Working_Hours = {s = "we need you to work more hours"} ;

  lin YouAgree_Job_Description x = {s = "you agree to work as a" ++ x.s} ;
  lin YouAgree_Leased_Car_Without_Leased_Car = {s = "you can do without a company car"} ;
  lin YouAgree_Pension_Fund x = {s = "you agree to a" ++ x.s ++ "% pension"} ;
  lin YouAgree_Promotion_Possibilities x = {s = "you agree to a" ++ x.s ++ "promotion track"} ;
  lin YouAgree_Salary x = {s = "you agree to" ++ x.s ++ "NIS per month"} ;
  lin YouAgree_Working_Hours x = {s = "you agree to work for" ++ x.s ++ "hours a day"} ;
}
