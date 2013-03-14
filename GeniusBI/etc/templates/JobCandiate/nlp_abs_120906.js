/* This Javascript file was automatically generated using PgfToJavascript.class from nlp_abs.pgf */

if (typeof grammars=='undefined') grammars=[];
grammars['nlp_abs']=[];

grammars['nlp_abs'].startcat='Action';
var lhs2rhs=[];
lhs2rhs['Float']=[];
lhs2rhs['Float']['nlp_biutee_eng_employer']=[];

lhs2rhs['Float']['nlp_biutee_candidate']=[];

lhs2rhs['Float']['nlp_biutee_eng_candidate']=[];

lhs2rhs['Float']['nlp_eng_candidate']=[];

lhs2rhs['Float']['nlp_eng_employer']=[];

lhs2rhs['Float']['nlp_biutee_genius']=[];

lhs2rhs['Float']['nlp_biutee_employer']=[];

lhs2rhs['Float']['nlp_biutee_eng_genius']=[];

lhs2rhs['Float']['nlp_eng_genius']=[];


lhs2rhs['PromotionTrack']=[];
lhs2rhs['PromotionTrack']['nlp_biutee_eng_employer']=[
	"{ adjective: fast }",
	"{ adjective: slow }"];

lhs2rhs['PromotionTrack']['nlp_biutee_candidate']=[
	"{ adjective }",
	"{ adjective }"];

lhs2rhs['PromotionTrack']['nlp_biutee_eng_candidate']=[
	"{ adjective: fast }",
	"{ adjective: slow }"];

lhs2rhs['PromotionTrack']['nlp_eng_candidate']=[
	"fast",
	"slow"];

lhs2rhs['PromotionTrack']['nlp_eng_employer']=[
	"fast",
	"slow"];

lhs2rhs['PromotionTrack']['nlp_biutee_genius']=[
	"{ adjective }",
	"{ adjective }"];

lhs2rhs['PromotionTrack']['nlp_biutee_employer']=[
	"{ adjective }",
	"{ adjective }"];

lhs2rhs['PromotionTrack']['nlp_biutee_eng_genius']=[
	"{ adjective: Fast }",
	"{ adjective: Slow }"];

lhs2rhs['PromotionTrack']['nlp_eng_genius']=[
	"Fast",
	"Slow"];


lhs2rhs['Demand']=[];
lhs2rhs['Demand']['nlp_biutee_eng_employer']=[
	"n:I:n<nsubj<v:offer:v>ccomp>v:work:v>prep>p:as:p>pobj>n: <JobDescription> :n",
	"I demand that you get a company car",
	"I do not offer a company car",
	"Let's discuss the car issue later",
	"I can give <Pension> % pension",
	"Let's discuss the pension issue later",
	"I can offer you a <PromotionTrack> promotion track",
	"Let's discuss the promotion possibilities later",
	"I want you to work for <Salary> NIS per month",
	"I want you to work for <WorkingHours> hours a day"];

lhs2rhs['Demand']['nlp_biutee_candidate']=[
	"I want to work as a <JobDescription>",
	"I need a company car",
	"I do not want a company car",
	"Let's discuss the car issue later",
	"I want <Pension> % pension",
	"Let's discuss the pension issue later",
	"I want a <PromotionTrack> promotion track",
	"Let's discuss the promotion possibilities later",
	"I would like <Salary> NIS per month",
	"I want a daily schedule of <WorkingHours> hours"];

lhs2rhs['Demand']['nlp_biutee_eng_candidate']=[
	"I want to work as a <JobDescription>",
	"I need a company car",
	"I do not want a company car",
	"Let's discuss the car issue later",
	"I want <Pension> % pension",
	"Let's discuss the pension issue later",
	"I want a <PromotionTrack> promotion track",
	"Let's discuss the promotion possibilities later",
	"I would like <Salary> NIS per month",
	"I want a daily schedule of <WorkingHours> hours"];

lhs2rhs['Demand']['nlp_eng_candidate']=[
	"I want to work as a <JobDescription>",
	"I need a company car",
	"I do not want a company car",
	"Let's discuss the car issue later",
	"I want <Pension> % pension",
	"Let's discuss the pension issue later",
	"I want a <PromotionTrack> promotion track",
	"Let's discuss the promotion possibilities later",
	"I would like <Salary> NIS per month",
	"I want a daily schedule of <WorkingHours> hours"];

lhs2rhs['Demand']['nlp_eng_employer']=[
	"n:I:n<nsubj<v:offer:v>ccomp>v:work:v>prep>p:as:p>pobj>n: <JobDescription> :n",
	"I demand that you get a company car",
	"I do not offer a company car",
	"Let's discuss the car issue later",
	"I can give <Pension> % pension",
	"Let's discuss the pension issue later",
	"I can offer you a <PromotionTrack> promotion track",
	"Let's discuss the promotion possibilities later",
	"I want you to work for <Salary> NIS per month",
	"I want you to work for <WorkingHours> hours a day"];

lhs2rhs['Demand']['nlp_biutee_genius']=[
	"Job Description : <JobDescription>",
	"Leased Car : With leased car",
	"Leased Car : Without leased car",
	"Leased Car : No agreement",
	"Pension Fund : <Pension> %",
	"Pension Fund : No agreement",
	"Promotion Possibilities : <PromotionTrack> promotion track",
	"Promotion Possibilities : No agreement",
	"Salary : <Salary> NIS",
	"Working Hours : <WorkingHours> hours"];

lhs2rhs['Demand']['nlp_biutee_employer']=[
	"n:I:n<nsubj<v:offer:v>ccomp>v:work:v>prep>p:as:p>pobj>n: <JobDescription> :n",
	"I demand that you get a company car",
	"I do not offer a company car",
	"Let's discuss the car issue later",
	"I can give <Pension> % pension",
	"Let's discuss the pension issue later",
	"I can offer you a <PromotionTrack> promotion track",
	"Let's discuss the promotion possibilities later",
	"I want you to work for <Salary> NIS per month",
	"I want you to work for <WorkingHours> hours a day"];

lhs2rhs['Demand']['nlp_biutee_eng_genius']=[
	"Job Description : <JobDescription>",
	"Leased Car : With leased car",
	"Leased Car : Without leased car",
	"Leased Car : No agreement",
	"Pension Fund : <Pension> %",
	"Pension Fund : No agreement",
	"Promotion Possibilities : <PromotionTrack> promotion track",
	"Promotion Possibilities : No agreement",
	"Salary : <Salary> NIS",
	"Working Hours : <WorkingHours> hours"];

lhs2rhs['Demand']['nlp_eng_genius']=[
	"Job Description : <JobDescription>",
	"Leased Car : With leased car",
	"Leased Car : Without leased car",
	"Leased Car : No agreement",
	"Pension Fund : <Pension> %",
	"Pension Fund : No agreement",
	"Promotion Possibilities : <PromotionTrack> promotion track",
	"Promotion Possibilities : No agreement",
	"Salary : <Salary> NIS",
	"Working Hours : <WorkingHours> hours"];


lhs2rhs['YouAgree']=[];
lhs2rhs['YouAgree']['nlp_biutee_eng_employer']=[
	"you agree to work as a <JobDescription>",
	"you can do without a company car",
	null,
	"you agree to a <Pension> % pension",
	"you agree to a <PromotionTrack> promotion track",
	"you agree to <Salary> NIS per month",
	"you agree to work for <WorkingHours> hours a day"];

lhs2rhs['YouAgree']['nlp_biutee_candidate']=[
	"you agree that I work as a <JobDescription>",
	null,
	"you promise me a company car",
	"you agree to a <Pension> % pension",
	"you agree to a <PromotionTrack> promotion track",
	"you give me <Salary> NIS per month",
	"you agree that I work <WorkingHours> hours a day"];

lhs2rhs['YouAgree']['nlp_biutee_eng_candidate']=[
	"you agree that I work as a <JobDescription>",
	null,
	"you promise me a company car",
	"you agree to a <Pension> % pension",
	"you agree to a <PromotionTrack> promotion track",
	"you give me <Salary> NIS per month",
	"you agree that I work <WorkingHours> hours a day"];

lhs2rhs['YouAgree']['nlp_eng_candidate']=[
	"you agree that I work as a <JobDescription>",
	null,
	"you promise me a company car",
	"you agree to a <Pension> % pension",
	"you agree to a <PromotionTrack> promotion track",
	"you give me <Salary> NIS per month",
	"you agree that I work <WorkingHours> hours a day"];

lhs2rhs['YouAgree']['nlp_eng_employer']=[
	"you agree to work as a <JobDescription>",
	"you can do without a company car",
	null,
	"you agree to a <Pension> % pension",
	"you agree to a <PromotionTrack> promotion track",
	"you agree to <Salary> NIS per month",
	"you agree to work for <WorkingHours> hours a day"];

lhs2rhs['YouAgree']['nlp_biutee_genius']=[
	"Job Description : <JobDescription>",
	"Leased Car : Without leased car",
	"Leased Car : With leased car",
	"Pension Fund : <Pension>",
	"Promotion Possibilities : <PromotionTrack>",
	"Salary: <Salary>",
	"Working Hours : <WorkingHours>"];

lhs2rhs['YouAgree']['nlp_biutee_employer']=[
	"you agree to work as a <JobDescription>",
	"you can do without a company car",
	null,
	"you agree to a <Pension> % pension",
	"you agree to a <PromotionTrack> promotion track",
	"you agree to <Salary> NIS per month",
	"you agree to work for <WorkingHours> hours a day"];

lhs2rhs['YouAgree']['nlp_biutee_eng_genius']=[
	"Job Description : <JobDescription>",
	"Leased Car : Without leased car",
	"Leased Car : With leased car",
	"Pension Fund : <Pension>",
	"Promotion Possibilities : <PromotionTrack>",
	"Salary: <Salary>",
	"Working Hours : <WorkingHours>"];

lhs2rhs['YouAgree']['nlp_eng_genius']=[
	"Job Description : <JobDescription>",
	"Leased Car : Without leased car",
	"Leased Car : With leased car",
	"Pension Fund : <Pension>",
	"Promotion Possibilities : <PromotionTrack>",
	"Salary: <Salary>",
	"Working Hours : <WorkingHours>"];


lhs2rhs['Other']=[];
lhs2rhs['Other']['nlp_biutee_eng_employer']=[
	"I am waiting",
	"Time is passing",
	"{ any: <String> }",
	"hi",
	"my name is { any: <String> }"];

lhs2rhs['Other']['nlp_biutee_candidate']=[
	"I am waiting",
	"Time is passing",
	"{ any }",
	"hi",
	"my name is { any }"];

lhs2rhs['Other']['nlp_biutee_eng_candidate']=[
	"I am waiting",
	"Time is passing",
	"{ any: <String> }",
	"hi",
	"my name is { any: <String> }"];

lhs2rhs['Other']['nlp_eng_candidate']=[
	"I am waiting",
	"Time is passing",
	"<String>",
	"hi",
	"my name is <String>"];

lhs2rhs['Other']['nlp_eng_employer']=[
	"I am waiting",
	"Time is passing",
	"<String>",
	"hi",
	"my name is <String>"];

lhs2rhs['Other']['nlp_biutee_genius']=[
	"wait",
	"time",
	"{ any }",
	"hi",
	"name : { any }"];

lhs2rhs['Other']['nlp_biutee_employer']=[
	"I am waiting",
	"Time is passing",
	"{ any }",
	"hi",
	"my name is { any }"];

lhs2rhs['Other']['nlp_biutee_eng_genius']=[
	"wait",
	"time",
	"{ any: <String> }",
	"hi",
	"name : { any: <String> }"];

lhs2rhs['Other']['nlp_eng_genius']=[
	"wait",
	"time",
	"<String>",
	"hi",
	"name : <String>"];


lhs2rhs['Agree']=[];
lhs2rhs['Agree']['nlp_biutee_eng_employer']=[
	"I accept your offer",
	"I can offer you a <JobDescription> position",
	"I can give you a company car",
	null,
	"I can agree on <Pension> % pension",
	"I can agree to a <PromotionTrack> promotion track",
	"I can agree to give you <Salary> NIS per month",
	"I can agree on a work day of <WorkingHours> hours"];

lhs2rhs['Agree']['nlp_biutee_candidate']=[
	"I accept your offer",
	"I agree to work in a <JobDescription> position",
	null,
	"I can do without a company car",
	"I can agree on <Pension> % pension",
	"I can agree to a <PromotionTrack> promotion track",
	"I can agree to work for <Salary> NIS per month",
	"I can agree on a work day of <WorkingHours> hours"];

lhs2rhs['Agree']['nlp_biutee_eng_candidate']=[
	"I accept your offer",
	"I agree to work in a <JobDescription> position",
	null,
	"I can do without a company car",
	"I can agree on <Pension> % pension",
	"I can agree to a <PromotionTrack> promotion track",
	"I can agree to work for <Salary> NIS per month",
	"I can agree on a work day of <WorkingHours> hours"];

lhs2rhs['Agree']['nlp_eng_candidate']=[
	"I accept your offer",
	"I agree to work in a <JobDescription> position",
	null,
	"I can do without a company car",
	"I can agree on <Pension> % pension",
	"I can agree to a <PromotionTrack> promotion track",
	"I can agree to work for <Salary> NIS per month",
	"I can agree on a work day of <WorkingHours> hours"];

lhs2rhs['Agree']['nlp_eng_employer']=[
	"I accept your offer",
	"I can offer you a <JobDescription> position",
	"I can give you a company car",
	null,
	"I can agree on <Pension> % pension",
	"I can agree to a <PromotionTrack> promotion track",
	"I can agree to give you <Salary> NIS per month",
	"I can agree on a work day of <WorkingHours> hours"];

lhs2rhs['Agree']['nlp_biutee_genius']=[
	"general",
	"Job Description : <JobDescription>",
	"Leased Car : With leased car",
	"Leased Car : Without leased car",
	"Pension Fund : <Pension> %",
	"Promotion Possibilities : <PromotionTrack> promotion track",
	"Salary : <Salary> NIS",
	"Working Hours : <WorkingHours> hours"];

lhs2rhs['Agree']['nlp_biutee_employer']=[
	"I accept your offer",
	"I can offer you a <JobDescription> position",
	"I can give you a company car",
	null,
	"I can agree on <Pension> % pension",
	"I can agree to a <PromotionTrack> promotion track",
	"I can agree to give you <Salary> NIS per month",
	"I can agree on a work day of <WorkingHours> hours"];

lhs2rhs['Agree']['nlp_biutee_eng_genius']=[
	"general",
	"Job Description : <JobDescription>",
	"Leased Car : With leased car",
	"Leased Car : Without leased car",
	"Pension Fund : <Pension> %",
	"Promotion Possibilities : <PromotionTrack> promotion track",
	"Salary : <Salary> NIS",
	"Working Hours : <WorkingHours> hours"];

lhs2rhs['Agree']['nlp_eng_genius']=[
	"general",
	"Job Description : <JobDescription>",
	"Leased Car : With leased car",
	"Leased Car : Without leased car",
	"Pension Fund : <Pension> %",
	"Promotion Possibilities : <PromotionTrack> promotion track",
	"Salary : <Salary> NIS",
	"Working Hours : <WorkingHours> hours"];


lhs2rhs['Append']=[];
lhs2rhs['Append']['nlp_biutee_eng_employer']=[
	"In addition to what I offered before"];

lhs2rhs['Append']['nlp_biutee_candidate']=[
	"In addition to what I offered before"];

lhs2rhs['Append']['nlp_biutee_eng_candidate']=[
	"In addition to what I offered before"];

lhs2rhs['Append']['nlp_eng_candidate']=[
	"In addition to what I offered before"];

lhs2rhs['Append']['nlp_eng_employer']=[
	"In addition to what I offered before"];

lhs2rhs['Append']['nlp_biutee_genius']=[
	"general"];

lhs2rhs['Append']['nlp_biutee_employer']=[
	"In addition to what I offered before"];

lhs2rhs['Append']['nlp_biutee_eng_genius']=[
	"general"];

lhs2rhs['Append']['nlp_eng_genius']=[
	"general"];


lhs2rhs['Quit']=[];
lhs2rhs['Quit']['nlp_biutee_eng_employer']=[
	"I must leave this negotiation without an agreement"];

lhs2rhs['Quit']['nlp_biutee_candidate']=[
	"I must leave this negotiation without an agreement"];

lhs2rhs['Quit']['nlp_biutee_eng_candidate']=[
	"I must leave this negotiation without an agreement"];

lhs2rhs['Quit']['nlp_eng_candidate']=[
	"I must leave this negotiation without an agreement"];

lhs2rhs['Quit']['nlp_eng_employer']=[
	"I must leave this negotiation without an agreement"];

lhs2rhs['Quit']['nlp_biutee_genius']=[
	"general"];

lhs2rhs['Quit']['nlp_biutee_employer']=[
	"I must leave this negotiation without an agreement"];

lhs2rhs['Quit']['nlp_biutee_eng_genius']=[
	"general"];

lhs2rhs['Quit']['nlp_eng_genius']=[
	"general"];


lhs2rhs['Happiness']=[];
lhs2rhs['Happiness']['nlp_biutee_eng_employer']=[
	"Excellent",
	"Good",
	"Great",
	"I am happy that you agree"];

lhs2rhs['Happiness']['nlp_biutee_candidate']=[
	"Excellent",
	"Good",
	"Great",
	"I am happy that you agree"];

lhs2rhs['Happiness']['nlp_biutee_eng_candidate']=[
	"Excellent",
	"Good",
	"Great",
	"I am happy that you agree"];

lhs2rhs['Happiness']['nlp_eng_candidate']=[
	"Excellent",
	"Good",
	"Great",
	"I am happy that you agree"];

lhs2rhs['Happiness']['nlp_eng_employer']=[
	"Excellent",
	"Good",
	"Great",
	"I am happy that you agree"];

lhs2rhs['Happiness']['nlp_biutee_genius']=[
	"happiness",
	"happiness",
	"happiness",
	"happiness"];

lhs2rhs['Happiness']['nlp_biutee_employer']=[
	"Excellent",
	"Good",
	"Great",
	"I am happy that you agree"];

lhs2rhs['Happiness']['nlp_biutee_eng_genius']=[
	"happiness",
	"happiness",
	"happiness",
	"happiness"];

lhs2rhs['Happiness']['nlp_eng_genius']=[
	"happiness",
	"happiness",
	"happiness",
	"happiness"];


lhs2rhs['JobDescription']=[];
lhs2rhs['JobDescription']['nlp_biutee_eng_employer']=[
	"{ noun: Programmer }",
	"{ noun: Project Manager }",
	"{ noun: QA }",
	"{ noun: <String> }",
	"{ noun: Team Manager }"];

lhs2rhs['JobDescription']['nlp_biutee_candidate']=[
	"{ noun }",
	"{ noun }",
	"{ noun }",
	"{ noun }",
	"{ noun }"];

lhs2rhs['JobDescription']['nlp_biutee_eng_candidate']=[
	"{ noun: Programmer }",
	"{ noun: Project Manager }",
	"{ noun: QA }",
	"{ noun: <String> }",
	"{ noun: Team Manager }"];

lhs2rhs['JobDescription']['nlp_eng_candidate']=[
	"Programmer",
	"Project Manager",
	"QA",
	"<String>",
	"Team Manager"];

lhs2rhs['JobDescription']['nlp_eng_employer']=[
	"Programmer",
	"Project Manager",
	"QA",
	"<String>",
	"Team Manager"];

lhs2rhs['JobDescription']['nlp_biutee_genius']=[
	"{ noun }",
	"{ noun }",
	"{ noun }",
	"{ noun }",
	"{ noun }"];

lhs2rhs['JobDescription']['nlp_biutee_employer']=[
	"{ noun }",
	"{ noun }",
	"{ noun }",
	"{ noun }",
	"{ noun }"];

lhs2rhs['JobDescription']['nlp_biutee_eng_genius']=[
	"{ noun: Programmer }",
	"{ noun: Project Manager }",
	"{ noun: QA }",
	"{ noun: <String> }",
	"{ noun: Team Manager }"];

lhs2rhs['JobDescription']['nlp_eng_genius']=[
	"Programmer",
	"Project Manager",
	"QA",
	"<String>",
	"Team Manager"];


lhs2rhs['Pension']=[];
lhs2rhs['Pension']['nlp_biutee_eng_employer']=[
	"{ number: 0 }",
	"{ number: 10 }",
	"{ number: 20 }",
	"{ number: <Int> }"];

lhs2rhs['Pension']['nlp_biutee_candidate']=[
	"{ number }",
	"{ number }",
	"{ number }",
	"{ number }"];

lhs2rhs['Pension']['nlp_biutee_eng_candidate']=[
	"{ number: 0 }",
	"{ number: 10 }",
	"{ number: 20 }",
	"{ number: <Int> }"];

lhs2rhs['Pension']['nlp_eng_candidate']=[
	"0",
	"10",
	"20",
	"<Int>"];

lhs2rhs['Pension']['nlp_eng_employer']=[
	"0",
	"10",
	"20",
	"<Int>"];

lhs2rhs['Pension']['nlp_biutee_genius']=[
	"{ number }",
	"{ number }",
	"{ number }",
	"{ number }"];

lhs2rhs['Pension']['nlp_biutee_employer']=[
	"{ number }",
	"{ number }",
	"{ number }",
	"{ number }"];

lhs2rhs['Pension']['nlp_biutee_eng_genius']=[
	"{ number: 0 }",
	"{ number: 10 }",
	"{ number: 20 }",
	"{ number: <Int> }"];

lhs2rhs['Pension']['nlp_eng_genius']=[
	"0",
	"10",
	"20",
	"<Int>"];


lhs2rhs['Misunderstanding']=[];
lhs2rhs['Misunderstanding']['nlp_biutee_eng_employer']=[
	"Sorry, I didn't understand you",
	"<String> is not one of the issues in our discussion",
	"<String> is not one of the valid values for <Issue>"];

lhs2rhs['Misunderstanding']['nlp_biutee_candidate']=[
	"Sorry, I didn't understand you",
	"<String> is not one of the issues in our discussion",
	"<String> is not one of the valid values for <Issue>"];

lhs2rhs['Misunderstanding']['nlp_biutee_eng_candidate']=[
	"Sorry, I didn't understand you",
	"<String> is not one of the issues in our discussion",
	"<String> is not one of the valid values for <Issue>"];

lhs2rhs['Misunderstanding']['nlp_eng_candidate']=[
	"Sorry, I didn't understand you",
	"<String> is not one of the issues in our discussion",
	"<String> is not one of the valid values for <Issue>"];

lhs2rhs['Misunderstanding']['nlp_eng_employer']=[
	"Sorry, I didn't understand you",
	"<String> is not one of the issues in our discussion",
	"<String> is not one of the valid values for <Issue>"];

lhs2rhs['Misunderstanding']['nlp_biutee_genius']=[
	"general",
	"misunderstanding : issue : <String>",
	"misunderstanding : value : <String> : <Issue>"];

lhs2rhs['Misunderstanding']['nlp_biutee_employer']=[
	"Sorry, I didn't understand you",
	"<String> is not one of the issues in our discussion",
	"<String> is not one of the valid values for <Issue>"];

lhs2rhs['Misunderstanding']['nlp_biutee_eng_genius']=[
	"general",
	"misunderstanding : issue : <String>",
	"misunderstanding : value : <String> : <Issue>"];

lhs2rhs['Misunderstanding']['nlp_eng_genius']=[
	"general",
	"misunderstanding : issue : <String>",
	"misunderstanding : value : <String> : <Issue>"];


lhs2rhs['Issue']=[];
lhs2rhs['Issue']['nlp_biutee_eng_employer']=[
	"{ noun: job description }",
	"{ noun: leased car }",
	"{ noun: pension }",
	"{ noun: promotion track }",
	"{ noun: salary }",
	"{ noun: working hours }"];

lhs2rhs['Issue']['nlp_biutee_candidate']=[
	"{ noun }",
	"{ noun }",
	"{ noun }",
	"{ noun }",
	"{ noun }",
	"{ noun }"];

lhs2rhs['Issue']['nlp_biutee_eng_candidate']=[
	"{ noun: job description }",
	"{ noun: leased car }",
	"{ noun: pension }",
	"{ noun: promotion track }",
	"{ noun: salary }",
	"{ noun: working hours }"];

lhs2rhs['Issue']['nlp_eng_candidate']=[
	"job description",
	"leased car",
	"pension",
	"promotion track",
	"salary",
	"working hours"];

lhs2rhs['Issue']['nlp_eng_employer']=[
	"job description",
	"leased car",
	"pension",
	"promotion track",
	"salary",
	"working hours"];

lhs2rhs['Issue']['nlp_biutee_genius']=[
	"{ noun }",
	"{ noun }",
	"{ noun }",
	"{ noun }",
	"{ noun }",
	"{ noun }"];

lhs2rhs['Issue']['nlp_biutee_employer']=[
	"{ noun }",
	"{ noun }",
	"{ noun }",
	"{ noun }",
	"{ noun }",
	"{ noun }"];

lhs2rhs['Issue']['nlp_biutee_eng_genius']=[
	"{ noun: Job Description }",
	"{ noun: Leased Car }",
	"{ noun: Pension Fund }",
	"{ noun: Promotion Possibilities }",
	"{ noun: Salary }",
	"{ noun: Working Hours }"];

lhs2rhs['Issue']['nlp_eng_genius']=[
	"Job Description",
	"Leased Car",
	"Pension Fund",
	"Promotion Possibilities",
	"Salary",
	"Working Hours"];


lhs2rhs['Insist']=[];
lhs2rhs['Insist']['nlp_biutee_eng_employer']=[
	"I insist on my previous offer",
	"I insist on my previous <Issue> offer"];

lhs2rhs['Insist']['nlp_biutee_candidate']=[
	"I insist on my previous offer",
	"I insist on my previous <Issue> offer"];

lhs2rhs['Insist']['nlp_biutee_eng_candidate']=[
	"I insist on my previous offer",
	"I insist on my previous <Issue> offer"];

lhs2rhs['Insist']['nlp_eng_candidate']=[
	"I insist on my previous offer",
	"I insist on my previous <Issue> offer"];

lhs2rhs['Insist']['nlp_eng_employer']=[
	"I insist on my previous offer",
	"I insist on my previous <Issue> offer"];

lhs2rhs['Insist']['nlp_biutee_genius']=[
	"general",
	"<Issue>"];

lhs2rhs['Insist']['nlp_biutee_employer']=[
	"I insist on my previous offer",
	"I insist on my previous <Issue> offer"];

lhs2rhs['Insist']['nlp_biutee_eng_genius']=[
	"general",
	"<Issue>"];

lhs2rhs['Insist']['nlp_eng_genius']=[
	"general",
	"<Issue>"];


lhs2rhs['Action']=[];
lhs2rhs['Action']['nlp_biutee_eng_employer']=[
	"<Demand>",
	"<Agree>",
	"<PartialAgree>",
	"<Insist>",
	"<Other>",
	"<Question>",
	"<Quit>",
	"<Reject>",
	"<Append>"];

lhs2rhs['Action']['nlp_biutee_candidate']=[
	"<Demand>",
	"<Agree>",
	"<PartialAgree>",
	"<Insist>",
	"<Other>",
	"<Question>",
	"<Quit>",
	"<Reject>",
	"<Append>"];

lhs2rhs['Action']['nlp_biutee_eng_candidate']=[
	"<Demand>",
	"<Agree>",
	"<PartialAgree>",
	"<Insist>",
	"<Other>",
	"<Question>",
	"<Quit>",
	"<Reject>",
	"<Append>"];

lhs2rhs['Action']['nlp_eng_candidate']=[
	"<Demand>",
	"<Agree>",
	"<PartialAgree>",
	"<Insist>",
	"<Other>",
	"<Question>",
	"<Quit>",
	"<Reject>",
	"<Append>"];

lhs2rhs['Action']['nlp_eng_employer']=[
	"<Demand>",
	"<Agree>",
	"<PartialAgree>",
	"<Insist>",
	"<Other>",
	"<Question>",
	"<Quit>",
	"<Reject>",
	"<Append>"];

lhs2rhs['Action']['nlp_biutee_genius']=[
	"< action : demand : <Demand> >",
	"< action : agree : <Agree> >",
	"< action : partial-agree : <PartialAgree> >",
	"< action : insist : <Insist> >",
	"< action : other : <Other> >",
	"< action : question : <Question> >",
	"< action : quit : <Quit> >",
	"< action : reject : <Reject> >",
	"< action : append : <Append> >"];

lhs2rhs['Action']['nlp_biutee_employer']=[
	"<Demand>",
	"<Agree>",
	"<PartialAgree>",
	"<Insist>",
	"<Other>",
	"<Question>",
	"<Quit>",
	"<Reject>",
	"<Append>"];

lhs2rhs['Action']['nlp_biutee_eng_genius']=[
	"< action : demand : <Demand> >",
	"< action : agree : <Agree> >",
	"< action : partial-agree : <PartialAgree> >",
	"< action : insist : <Insist> >",
	"< action : other : <Other> >",
	"< action : question : <Question> >",
	"< action : quit : <Quit> >",
	"< action : reject : <Reject> >",
	"< action : append : <Append> >"];

lhs2rhs['Action']['nlp_eng_genius']=[
	"< action : demand : <Demand> >",
	"< action : agree : <Agree> >",
	"< action : partial-agree : <PartialAgree> >",
	"< action : insist : <Insist> >",
	"< action : other : <Other> >",
	"< action : question : <Question> >",
	"< action : quit : <Quit> >",
	"< action : reject : <Reject> >",
	"< action : append : <Append> >"];


lhs2rhs['String']=[];
lhs2rhs['String']['nlp_biutee_eng_employer']=[];

lhs2rhs['String']['nlp_biutee_candidate']=[];

lhs2rhs['String']['nlp_biutee_eng_candidate']=[];

lhs2rhs['String']['nlp_eng_candidate']=[];

lhs2rhs['String']['nlp_eng_employer']=[];

lhs2rhs['String']['nlp_biutee_genius']=[];

lhs2rhs['String']['nlp_biutee_employer']=[];

lhs2rhs['String']['nlp_biutee_eng_genius']=[];

lhs2rhs['String']['nlp_eng_genius']=[];


lhs2rhs['Question']=[];
lhs2rhs['Question']['nlp_biutee_eng_employer']=[
	"do we agree",
	"Is there anything else we should discuss",
	"what are your demands",
	"what are your demands regarding <Issue>",
	"what would you like as your job description",
	"do you demand a leased car",
	"what promotion track is the best for you",
	"what are your salary demands",
	"how many hours would you like to work each day"];

lhs2rhs['Question']['nlp_biutee_candidate']=[
	"do we agree",
	"Is there anything else we should discuss",
	"what do you offer",
	"what do you offer regarding <Issue>",
	"what position do you offer",
	"do you give a company car",
	"what promotion track do you offer",
	"how much salary do you offer",
	"how many hours would I work each day"];

lhs2rhs['Question']['nlp_biutee_eng_candidate']=[
	"do we agree",
	"Is there anything else we should discuss",
	"what do you offer",
	"what do you offer regarding <Issue>",
	"what position do you offer",
	"do you give a company car",
	"what promotion track do you offer",
	"how much salary do you offer",
	"how many hours would I work each day"];

lhs2rhs['Question']['nlp_eng_candidate']=[
	"do we agree",
	"Is there anything else we should discuss",
	"what do you offer",
	"what do you offer regarding <Issue>",
	"what position do you offer",
	"do you give a company car",
	"what promotion track do you offer",
	"how much salary do you offer",
	"how many hours would I work each day"];

lhs2rhs['Question']['nlp_eng_employer']=[
	"do we agree",
	"Is there anything else we should discuss",
	"what are your demands",
	"what are your demands regarding <Issue>",
	"what would you like as your job description",
	"do you demand a leased car",
	"what promotion track is the best for you",
	"what are your salary demands",
	"how many hours would you like to work each day"];

lhs2rhs['Question']['nlp_biutee_genius']=[
	"agreement",
	"final",
	"initial",
	"<Issue>",
	"Job Description",
	"Leased Car",
	"Promotion Possibilities",
	"Salary",
	"Working Hours"];

lhs2rhs['Question']['nlp_biutee_employer']=[
	"do we agree",
	"Is there anything else we should discuss",
	"what are your demands",
	"what are your demands regarding <Issue>",
	"what would you like as your job description",
	"do you demand a leased car",
	"what promotion track is the best for you",
	"what are your salary demands",
	"how many hours would you like to work each day"];

lhs2rhs['Question']['nlp_biutee_eng_genius']=[
	"agreement",
	"final",
	"initial",
	"<Issue>",
	"Job Description",
	"Leased Car",
	"Promotion Possibilities",
	"Salary",
	"Working Hours"];

lhs2rhs['Question']['nlp_eng_genius']=[
	"agreement",
	"final",
	"initial",
	"<Issue>",
	"Job Description",
	"Leased Car",
	"Promotion Possibilities",
	"Salary",
	"Working Hours"];


lhs2rhs['WorkingHours']=[];
lhs2rhs['WorkingHours']['nlp_biutee_eng_employer']=[
	"{ number: 10 }",
	"{ number: 8 }",
	"{ number: 9 }",
	"{ number: <Float> }"];

lhs2rhs['WorkingHours']['nlp_biutee_candidate']=[
	"{ number }",
	"{ number }",
	"{ number }",
	"{ number }"];

lhs2rhs['WorkingHours']['nlp_biutee_eng_candidate']=[
	"{ number: 10 }",
	"{ number: 8 }",
	"{ number: 9 }",
	"{ number: <Float> }"];

lhs2rhs['WorkingHours']['nlp_eng_candidate']=[
	"10",
	"8",
	"9",
	"<Float>"];

lhs2rhs['WorkingHours']['nlp_eng_employer']=[
	"10",
	"8",
	"9",
	"<Float>"];

lhs2rhs['WorkingHours']['nlp_biutee_genius']=[
	"{ number }",
	"{ number }",
	"{ number }",
	"{ number }"];

lhs2rhs['WorkingHours']['nlp_biutee_employer']=[
	"{ number }",
	"{ number }",
	"{ number }",
	"{ number }"];

lhs2rhs['WorkingHours']['nlp_biutee_eng_genius']=[
	"{ number: 10 }",
	"{ number: 8 }",
	"{ number: 9 }",
	"{ number: <Float> }"];

lhs2rhs['WorkingHours']['nlp_eng_genius']=[
	"10",
	"8",
	"9",
	"<Float>"];


lhs2rhs['PartialAgree']=[];
lhs2rhs['PartialAgree']['nlp_biutee_eng_employer']=[
	"I partially accept your offer",
	"I accept your <Issue> offer"];

lhs2rhs['PartialAgree']['nlp_biutee_candidate']=[
	"I partially accept your offer",
	"I accept your <Issue> offer"];

lhs2rhs['PartialAgree']['nlp_biutee_eng_candidate']=[
	"I partially accept your offer",
	"I accept your <Issue> offer"];

lhs2rhs['PartialAgree']['nlp_eng_candidate']=[
	"I partially accept your offer",
	"I accept your <Issue> offer"];

lhs2rhs['PartialAgree']['nlp_eng_employer']=[
	"I partially accept your offer",
	"I accept your <Issue> offer"];

lhs2rhs['PartialAgree']['nlp_biutee_genius']=[
	"general",
	"<Issue>"];

lhs2rhs['PartialAgree']['nlp_biutee_employer']=[
	"I partially accept your offer",
	"I accept your <Issue> offer"];

lhs2rhs['PartialAgree']['nlp_biutee_eng_genius']=[
	"general",
	"<Issue>"];

lhs2rhs['PartialAgree']['nlp_eng_genius']=[
	"general",
	"<Issue>"];


lhs2rhs['Salary']=[];
lhs2rhs['Salary']['nlp_biutee_eng_employer']=[
	"{ number: 12,000 }",
	"{ number: 20,000 }",
	"{ number: 7,000 }",
	"{ number: <Int> }"];

lhs2rhs['Salary']['nlp_biutee_candidate']=[
	"{ number }",
	"{ number }",
	"{ number }",
	"{ number }"];

lhs2rhs['Salary']['nlp_biutee_eng_candidate']=[
	"{ number: 12,000 }",
	"{ number: 20,000 }",
	"{ number: 7,000 }",
	"{ number: <Int> }"];

lhs2rhs['Salary']['nlp_eng_candidate']=[
	"12,000",
	"20,000",
	"7,000",
	"<Int>"];

lhs2rhs['Salary']['nlp_eng_employer']=[
	"12,000",
	"20,000",
	"7,000",
	"<Int>"];

lhs2rhs['Salary']['nlp_biutee_genius']=[
	"{ number }",
	"{ number }",
	"{ number }",
	"{ number }"];

lhs2rhs['Salary']['nlp_biutee_employer']=[
	"{ number }",
	"{ number }",
	"{ number }",
	"{ number }"];

lhs2rhs['Salary']['nlp_biutee_eng_genius']=[
	"{ number: 12,000 }",
	"{ number: 20,000 }",
	"{ number: 7,000 }",
	"{ number: <Int> }"];

lhs2rhs['Salary']['nlp_eng_genius']=[
	"12,000",
	"20,000",
	"7,000",
	"<Int>"];


lhs2rhs['Reject']=[];
lhs2rhs['Reject']['nlp_biutee_eng_employer']=[
	"I cannot accept your offer",
	"I already accepted your conditions in <Int> issues, I expect that you compromise on other issues",
	"we do not need this job right now",
	"leased car is too expensive for us",
	"the pension you ask for is too high for us",
	"I do not want to commit to this promotion track",
	"the salary you ask for is too high",
	"we need you to work more hours"];

lhs2rhs['Reject']['nlp_biutee_candidate']=[
	"I cannot accept your offer",
	"I already accepted your conditions in <Int> issues, I expect that you compromise on other issues",
	"This job description is not good enough for me",
	"I must have a car to get to work",
	"the pension you offer is too low",
	"this promotion track is too slow",
	"the salary you offer is too low",
	"the number of daily working hours is too high"];

lhs2rhs['Reject']['nlp_biutee_eng_candidate']=[
	"I cannot accept your offer",
	"I already accepted your conditions in <Int> issues, I expect that you compromise on other issues",
	"This job description is not good enough for me",
	"I must have a car to get to work",
	"the pension you offer is too low",
	"this promotion track is too slow",
	"the salary you offer is too low",
	"the number of daily working hours is too high"];

lhs2rhs['Reject']['nlp_eng_candidate']=[
	"I cannot accept your offer",
	"I already accepted your conditions in <Int> issues, I expect that you compromise on other issues",
	"This job description is not good enough for me",
	"I must have a car to get to work",
	"the pension you offer is too low",
	"this promotion track is too slow",
	"the salary you offer is too low",
	"the number of daily working hours is too high"];

lhs2rhs['Reject']['nlp_eng_employer']=[
	"I cannot accept your offer",
	"I already accepted your conditions in <Int> issues, I expect that you compromise on other issues",
	"we do not need this job right now",
	"leased car is too expensive for us",
	"the pension you ask for is too high for us",
	"I do not want to commit to this promotion track",
	"the salary you ask for is too high",
	"we need you to work more hours"];

lhs2rhs['Reject']['nlp_biutee_genius']=[
	"general",
	"< reject : issuecount : <Int> >",
	"Job Description",
	"Leased Car",
	"Pension Fund",
	"Promotion Possibilities",
	"Salary",
	"Working Hours"];

lhs2rhs['Reject']['nlp_biutee_employer']=[
	"I cannot accept your offer",
	"I already accepted your conditions in <Int> issues, I expect that you compromise on other issues",
	"we do not need this job right now",
	"leased car is too expensive for us",
	"the pension you ask for is too high for us",
	"I do not want to commit to this promotion track",
	"the salary you ask for is too high",
	"we need you to work more hours"];

lhs2rhs['Reject']['nlp_biutee_eng_genius']=[
	"general",
	"< reject : issuecount : <Int> >",
	"Job Description",
	"Leased Car",
	"Pension Fund",
	"Promotion Possibilities",
	"Salary",
	"Working Hours"];

lhs2rhs['Reject']['nlp_eng_genius']=[
	"general",
	"< reject : issuecount : <Int> >",
	"Job Description",
	"Leased Car",
	"Pension Fund",
	"Promotion Possibilities",
	"Salary",
	"Working Hours"];


lhs2rhs['Int']=[];
lhs2rhs['Int']['nlp_biutee_eng_employer']=[];

lhs2rhs['Int']['nlp_biutee_candidate']=[];

lhs2rhs['Int']['nlp_biutee_eng_candidate']=[];

lhs2rhs['Int']['nlp_eng_candidate']=[];

lhs2rhs['Int']['nlp_eng_employer']=[];

lhs2rhs['Int']['nlp_biutee_genius']=[];

lhs2rhs['Int']['nlp_biutee_employer']=[];

lhs2rhs['Int']['nlp_biutee_eng_genius']=[];

lhs2rhs['Int']['nlp_eng_genius']=[];


lhs2rhs['Disagree']=[];
lhs2rhs['Disagree']['nlp_biutee_eng_employer']=[
	"I do not agree with you"];

lhs2rhs['Disagree']['nlp_biutee_candidate']=[
	"I do not agree with you"];

lhs2rhs['Disagree']['nlp_biutee_eng_candidate']=[
	"I do not agree with you"];

lhs2rhs['Disagree']['nlp_eng_candidate']=[
	"I do not agree with you"];

lhs2rhs['Disagree']['nlp_eng_employer']=[
	"I do not agree with you"];

lhs2rhs['Disagree']['nlp_biutee_genius']=[
	"general"];

lhs2rhs['Disagree']['nlp_biutee_employer']=[
	"I do not agree with you"];

lhs2rhs['Disagree']['nlp_biutee_eng_genius']=[
	"general"];

lhs2rhs['Disagree']['nlp_eng_genius']=[
	"general"];
grammars['nlp_abs'].lhs2rhs=lhs2rhs;
