---- nlp_incomplete_genius
------ nlp_eng_genius       (nlp_interface = nlp_instance_eng)
------ nlp_biutee_genius    (nlp_interface = nlp_instance_biutee)

incomplete concrete nlp_incomplete_genius of nlp_abs = open nlp_interface in {
  flags  coding = latin1;
  lincat Action = {s : Str};
  lin Action_Agree x = {s = "<"++"action"++":"++"agree"++":"++x.s++">"} ;
  lin Action_Demand x = {s = "<"++"action"++":"++"demand"++":"++x.s++">"} ;
  lin Action_Other x = {s = "<"++"action"++":"++"other"++":"++x.s++">"} ;
  lin Action_PartialAgree x = {s = "<"++"action"++":"++"partial-agree"++":"++x.s++">"} ;
  lin Action_Insist x = {s = "<"++"action"++":"++"insist"++":"++x.s++">"} ;
  lin Action_Question x = {s = "<"++"action"++":"++"question"++":"++x.s++">"} ;
  lin Action_Quit x = {s = "<"++"action"++":"++"quit"++":"++x.s++">"} ;
  lin Action_Reject x = {s = "<"++"action"++":"++"reject"++":"++x.s++">"} ;
  lin Action_Append x = {s = "<"++"action"++":"++"append"++":"++x.s++">"} ;
  lincat Append = {s : Str} ;
  lin Append_General = {s = "general"} ;
  lincat Agree = {s : Str} ;
  lin Agree_General = {s = "general"} ;
  lin Agree_Job_Description x = {s = "Job Description"++":"++x.s} ;
  lin Agree_Leased_Car_With_Leased_Car = {s = "Leased Car"++":"++"With leased car"} ;
  lin Agree_Leased_Car_Without_Leased_Car = {s = "Leased Car"++":"++"Without leased car"} ;
  lin Agree_Pension_Fund x = {s = "Pension Fund"++":"++x.s++"%"} ;
  lin Agree_Promotion_Possibilities x = {s = "Promotion Possibilities"++":"++x.s++"promotion track"} ;
  lin Agree_Salary x = {s = "Salary"++":"++x.s++"NIS"} ;
  lin Agree_Working_Hours x = {s = "Working Hours"++":"++x.s++"hours"} ;
  lincat Demand = {s : Str} ;
  lin Demand_Job_Description x = {s = "Job Description"++":"++x.s} ;
  lin Demand_Leased_Car_With_Leased_Car = {s = "Leased Car"++":"++"With leased car"} ;
  lin Demand_Leased_Car_Without_Leased_Car = {s = "Leased Car"++":"++"Without leased car"} ;
  lin Demand_Leased_Car_No_Agreement = {s = "Leased Car"++":"++"No agreement"} ;
  lin Demand_Pension_Fund x = {s = "Pension Fund"++":"++x.s++"%"} ;
  lin Demand_Pension_Fund_No_Agreement = {s = "Pension Fund"++":"++"No agreement"} ;
  lin Demand_Promotion_Possibilities x = {s = "Promotion Possibilities"++":"++x.s++"promotion track"} ;
  lin Demand_Promotion_Possibilities_No_Agreement = {s = "Promotion Possibilities"++":"++"No agreement"} ;
  lin Demand_Salary x = {s = "Salary"++":"++x.s++"NIS"} ;
  lin Demand_Working_Hours x = {s = "Working Hours"++":"++x.s++"hours"} ;
  lincat Disagree = {s : Str} ;
  lin DisagreeGeneral = {s = "general"} ;
  lincat Float = {s : Str} ;
  lincat Happiness = {s : Str} ;
  lin Happiness_Excellent = {s = "happiness"} ;
  lin Happiness_Good = {s = "happiness"} ;
  lin Happiness_Great = {s = "happiness"} ;
  lin Happiness_I_am_happy_that_you_agree = {s = "happiness"} ;
  lincat Int = {s : Str} ;

  lincat Issue = {s : Str} ;
  lin Issue_job_description = mkNoun "Job Description";
  lin Issue_leased_car = mkNoun "Leased Car";
  lin Issue_pension = mkNoun "Pension Fund";
  lin Issue_promotion_track = mkNoun "Promotion Possibilities";
  lin Issue_salary = mkNoun "Salary";
  lin Issue_working_hours = mkNoun "Working Hours";

  lincat JobDescription = {s : Str} ;
  lin JobDescription_Programmer = mkNoun "Programmer";
  lin JobDescription_Project_Manager = mkNoun "Project Manager";
  lin JobDescription_QA = mkNoun "QA";
  lin JobDescription_Team_Manager = mkNoun "Team Manager";
  lin JobDescription_String x = mkNoun x.s;

  lincat Misunderstanding = {s : Str} ;
  lin Misunderstanding_General = {s = "general"} ;
  lin Misunderstanding_Issue x = {s = "misunderstanding"++":"++"issue"++":"++ x.s} ;
  lin Misunderstanding_Value value issue = {s = "misunderstanding"++":"++"value"++":"++ value.s ++ ":" ++ issue.s} ;
  
  lincat Other = {s : Str} ;
  lin Other_I_am_waiting = {s = "wait"} ;
  lin Other_time_is_passing = {s = "time"} ;
  lin Other_String x = mkAny x.s;
  lin Other_hi = {s = "hi"} ;
  lin Other_my_name_is_String x = {s = "name" ++":"++ (mkAny x.s).s} ;

  lincat PartialAgree = {s : Str} ;
  lin PartialAgree_General = {s = "general"} ;
  lin PartialAgree_Issue x = {s = x.s} ;
  lincat Insist = {s : Str} ;
  lin Insist_General = {s = "general"} ;
  lin Insist_Issue x = {s = x.s} ;

  lincat Pension = {s : Str} ;
  lin Pension_0 = mkNumber "0";
  lin Pension_10 = mkNumber "10";
  lin Pension_20 = mkNumber "20";
  lin Pension_Int x = mkNumber x.s;

  lincat PromotionTrack = {s : Str} ;
  lin PromotionTrack_fast = mkAdjective "Fast";
  lin PromotionTrack_slow = mkAdjective "Slow";

  lincat Question = {s : Str} ;
  lin Question_Agreement = {s = "agreement"} ;
  lin Question_Final = {s = "final"} ;
  lin Question_Initial = {s = "initial"} ;
  lin Question_Issue x = { s = x.s } ;
  lin Question_Job_Description = {s = "Job Description"} ;
  lin Question_Leased_Car = {s = "Leased Car"} ;
  lin Question_Promotion_Possibilities = {s = "Promotion Possibilities"} ;
  lin Question_Salary = {s = "Salary"} ;
  lin Question_Working_Hours = {s = "Working Hours"} ;
  lincat Quit = {s : Str} ;
  lin QuitGeneral = {s = "general"} ;
  lincat Reject = {s : Str} ;
  lin Reject_General = {s = "general"} ;
  lin Reject_IssueCount x = {s = "<"++"reject"++":"++"issuecount"++":" ++ x.s ++ ">"} ;
  lin Reject_Job_Description = {s = "Job Description"} ;
  lin Reject_Leased_Car = {s = "Leased Car"} ;
  lin Reject_Pension_Fund = {s = "Pension Fund"} ;
  lin Reject_Promotion_Possibilities = {s = "Promotion Possibilities"} ;
  lin Reject_Salary = {s = "Salary"} ;
  lin Reject_Working_Hours = {s = "Working Hours"} ;

  lincat Salary = {s : Str} ;
  lin Salary_12000 = mkNumber "12,000";
  lin Salary_20000 = mkNumber "20,000";
  lin Salary_7000 =  mkNumber "7,000";
  lin Salary_Int x = mkNumber x.s;

  lincat String = {s : Str} ;

  lincat WorkingHours = {s : Str} ;
  lin WorkingHours_10 = mkNumber "10";
  lin WorkingHours_8 = mkNumber "8";
  lin WorkingHours_9 = mkNumber "9";
  lin WorkingHours_Float x = mkNumber x.s;

  lincat YouAgree = {s : Str} ;
  lin YouAgree_Job_Description x = {s = "Job Description"++":"++x.s} ;
  lin YouAgree_Leased_Car_Without_Leased_Car = {s = "Leased Car"++":"++"Without leased car"} ;
  lin YouAgree_Leased_Car_With_Leased_Car = {s = "Leased Car"++":"++"With leased car"} ;
  lin YouAgree_Pension_Fund x = {s = "Pension Fund"++":"++x.s} ;
  lin YouAgree_Promotion_Possibilities x = {s = "Promotion Possibilities"++":"++x.s} ;
  lin YouAgree_Salary x = {s = "Salary:"++x.s} ;
  lin YouAgree_Working_Hours x = {s = "Working Hours"++":"++x.s} ;
}
