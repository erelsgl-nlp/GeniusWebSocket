---- nlp_incomplete_candidate
------ nlp_eng_candidate       (nlp_interface = nlp_instance_eng)
------ nlp_biutee_candidate    (nlp_interface = nlp_instance_biutee)

incomplete concrete nlp_incomplete_candidate of nlp_abs = nlp_incomplete ** {
  lin Agree_Pension_Fund x = {s = "I can agree on" ++ x.s ++ "% pension"} ;
  lin Agree_Promotion_Possibilities x = {s = "I can agree to a" ++ x.s ++ "promotion track"} ;
  lin Agree_Working_Hours x = {s = "I can agree on a work day of" ++ x.s ++ "hours"} ;
  lin Agree_Job_Description x = {s = "I agree to work in a" ++ x.s ++ "position"} ;
  lin Agree_Leased_Car_Without_Leased_Car = {s = "I can do without a company car"} ;
  lin Agree_Salary x = {s = "I can agree to work for" ++ x.s ++ "NIS per month"} ;
 
  lin Demand_Job_Description x = {s = "I want to work as a" ++ x.s} ;
  lin Demand_Leased_Car_With_Leased_Car = {s = " I need a company car"} ;
  lin Demand_Leased_Car_Without_Leased_Car = {s = "I do not want a company car"} ;
  lin Demand_Pension_Fund x = {s = "I want" ++ x.s ++ "% pension"} ;
  lin Demand_Promotion_Possibilities x = {s = "I want a" ++ x.s ++ "promotion track"} ;
  lin Demand_Salary x = {s = "I would like" ++ x.s ++ "NIS per month"} ;
  lin Demand_Working_Hours x = {s = "I want a daily schedule of" ++ x.s ++ "hours"} ;

  lin Question_Initial = {s = "what do you offer"} ;
  lin Question_Issue x = {s = "what do you offer regarding" ++ x.s} ;
  lin Question_Job_Description = {s = "what position do you offer"} ;
  lin Question_Leased_Car = {s = "do you give a company car"} ;
  lin Question_Promotion_Possibilities = {s = "what promotion track do you offer"} ;
  lin Question_Salary = {s = "how much salary do you offer"} ;
  lin Question_Working_Hours = {s = "how many hours would I work each day"} ;
  
  lin Reject_Job_Description = {s = "This job description is not good enough for me"} ;
  lin Reject_Leased_Car = {s = "I must have a car to get to work"} ;
  lin Reject_Pension_Fund = {s = "the pension you offer is too low"} ;
  lin Reject_Promotion_Possibilities = {s = "this promotion track is too slow"} ;
  lin Reject_Salary = {s = "the salary you offer is too low"} ;
  lin Reject_Working_Hours = {s = "the number of daily working hours is too high"} ;

  lin YouAgree_Job_Description x = {s = "you agree that I work as a" ++ x.s} ;
  lin YouAgree_Leased_Car_With_Leased_Car = {s = "you promise me a company car"} ;
  lin YouAgree_Pension_Fund x = {s = "you agree to a" ++ x.s ++ "% pension"} ;
  lin YouAgree_Promotion_Possibilities x = {s = "you agree to a" ++ x.s ++ "promotion track"} ;
  lin YouAgree_Salary x = {s = "you give me" ++ x.s ++ "NIS per month"} ;
  lin YouAgree_Working_Hours x = {s = "you agree that I work" ++ x.s ++ "hours a day"} ;
}
