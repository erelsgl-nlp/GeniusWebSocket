abstract nlp_abs = {
  flags
    startcat = Action ;
    coding = latin1 ;
  cat Action ;
  data Action_Demand : Demand -> Action ;
  data Action_Agree : Agree -> Action ;
  data Action_PartialAgree : PartialAgree -> Action ;
  data Action_Insist : Insist -> Action ;
  data Action_Other : Other -> Action ;
  data Action_Question : Question -> Action ;
  data Action_Quit : Quit -> Action ;
  data Action_Reject : Reject -> Action ;
  data Action_Append : Append -> Action ;
  cat Append ;
  data Append_General : Append ;
  cat Agree ;
  data Agree_General : Agree ;
  data Agree_Job_Description : JobDescription -> Agree ;
  data Agree_Leased_Car_With_Leased_Car : Agree ;
  data Agree_Leased_Car_Without_Leased_Car : Agree ;
  data Agree_Pension_Fund : Pension -> Agree ;
  data Agree_Promotion_Possibilities : PromotionTrack -> Agree ;
  data Agree_Salary : Salary -> Agree ;
  data Agree_Working_Hours : WorkingHours -> Agree ;
  cat Demand ;
  data Demand_Job_Description : JobDescription -> Demand ;
  data Demand_Leased_Car_With_Leased_Car : Demand ;
  data Demand_Leased_Car_Without_Leased_Car : Demand ;
  data Demand_Leased_Car_No_Agreement : Demand ;
  data Demand_Pension_Fund : Pension -> Demand ;
  data Demand_Pension_Fund_No_Agreement : Demand ;
  data Demand_Promotion_Possibilities : PromotionTrack -> Demand ;
  data Demand_Promotion_Possibilities_No_Agreement : Demand ;
  data Demand_Salary : Salary -> Demand ;
  data Demand_Working_Hours : WorkingHours -> Demand ;
  cat Disagree ;
  data DisagreeGeneral : Disagree ;
  cat Float ;
  cat Happiness ;
  data Happiness_Excellent : Happiness ;
  data Happiness_Good : Happiness ;
  data Happiness_Great : Happiness ;
  data Happiness_I_am_happy_that_you_agree : Happiness ;
  cat Int ;
  cat Issue ;
  data Issue_job_description : Issue ;
  data Issue_leased_car : Issue ;
  data Issue_pension : Issue ;
  data Issue_promotion_track : Issue ;
  data Issue_salary : Issue ;
  data Issue_working_hours : Issue ;
  cat JobDescription ;
  data JobDescription_Programmer : JobDescription ;
  data JobDescription_Project_Manager : JobDescription ;
  data JobDescription_QA : JobDescription ;
  data JobDescription_String : String -> JobDescription ;
  data JobDescription_Team_Manager : JobDescription ;
  cat Misunderstanding ;
  data Misunderstanding_General : Misunderstanding ;
  data Misunderstanding_Issue : String -> Misunderstanding ;
  data Misunderstanding_Value : String -> Issue -> Misunderstanding ;
  cat Other ;
  data Other_I_am_waiting : Other ;
  data Other_time_is_passing : Other ;
  data Other_String : String -> Other ;
  data Other_hi : Other ;
  data Other_my_name_is_String : String -> Other ;
  cat PartialAgree ;
  data PartialAgree_General : PartialAgree ;
  data PartialAgree_Issue : Issue -> PartialAgree ;
  cat Insist ;
  data Insist_General : Insist ;
  data Insist_Issue : Issue -> Insist ;
  cat Pension ;
  data Pension_0 : Pension ;
  data Pension_10 : Pension ;
  data Pension_20 : Pension ;
  data Pension_Int : Int -> Pension ;
  cat PromotionTrack ;
  data PromotionTrack_fast : PromotionTrack ;
  data PromotionTrack_slow : PromotionTrack ;
  cat Question ;
  data Question_Agreement : Question ;
  data Question_Final : Question ;
  data Question_Initial : Question ;
  data Question_Issue : Issue -> Question ;
  data Question_Job_Description : Question ;
  data Question_Leased_Car : Question ;
  data Question_Promotion_Possibilities : Question ;
  data Question_Salary : Question ;
  data Question_Working_Hours : Question ;
  cat Quit ;
  data QuitGeneral : Quit ;
  cat Reject ;
  data Reject_General : Reject ;
  data Reject_IssueCount : Int -> Reject ;
  data Reject_Job_Description : Reject ;
  data Reject_Leased_Car : Reject ;
  data Reject_Pension_Fund : Reject ;
  data Reject_Promotion_Possibilities : Reject ;
  data Reject_Salary : Reject ;
  data Reject_Working_Hours : Reject ;
  cat Salary ;
  data Salary_12000 : Salary ;
  data Salary_20000 : Salary ;
  data Salary_7000 : Salary ;
  data Salary_Int : Int -> Salary ;
  cat String ;
  cat WorkingHours ;
  data WorkingHours_10 : WorkingHours ;
  data WorkingHours_8 : WorkingHours ;
  data WorkingHours_9 : WorkingHours ;
  data WorkingHours_Float : Float -> WorkingHours ;
  cat YouAgree ;
  data YouAgree_Job_Description : JobDescription -> YouAgree ;
  data YouAgree_Leased_Car_Without_Leased_Car : YouAgree ;
  data YouAgree_Leased_Car_With_Leased_Car : YouAgree ;
  data YouAgree_Pension_Fund : Pension -> YouAgree ;
  data YouAgree_Promotion_Possibilities : PromotionTrack -> YouAgree ;
  data YouAgree_Salary : Salary -> YouAgree ;
  data YouAgree_Working_Hours : WorkingHours -> YouAgree ;
}
-- gt | l
-- gt -cat=Demand | l
-- gt -cat=Salary | l
