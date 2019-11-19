// Agent sample_agent in project testing.mas2j

/* Initial beliefs and rules */

/* Initial goals */

//!start.
!plays(initiator,doctor).
//+!start :true<-
//	.my_name(Name);
//	myAction(Name);
	//.print("Hello world!").
	
+!plays(initiator,In)
 		 : .my_name(Me)
 		 <- .send(doctor,tell,introduction(participant,Me)). 
		 
	
+percept(demo):true<-
	.print("this worked!"). 
	
+hello[source(A)]
	<- .print("I received a 'hello' from ",A).
	
//+!plays(initiator,doctor)[source(self)]
//   :  .my_name(Me)
//   <- .print("hello")
 //  .send(doctor,tell,contract(participant,Me)).
	// answer to Call For Proposal
	
	/*
@c1 +cfp(CNPId,Task,C,NC)[source(A)]
   :  plays(initiator,A) & price(Task,Offer)
   <- +proposal(CNPId,Task,C,NC,Offer);		// remember my proposal
      .send(A,tell,propose(CNPId,Offer)).
	  
	  */
	  
	   // Handling an Accept message
	   /*
@r1 +accept_proposal(CNPId)
		: proposal(CNPId,Task,Offer)
		<- .print("My proposal ", Offer, " won CNP ",
		 	 	 	 	 	 CNPId, " for ", Task, “!").
		 // do the task and report to initiator

 // Handling a Reject message


// Handling an Accept message
*/

// answer to Call For Proposal
@c1 +!cfp(CNPId,Task,C,NC)[source(A)]
   :  plays(initiator,A) & price(Task,Offer)
   <- +proposal(CNPId,Task,C,NC,Offer);		// remember my proposal
      .send(A,tell,propose(CNPId,Offer)).
	  
// Handling an Accept message
@r1 +!accept_proposal(CNPId)[source(A)]
		: proposal(CNPId,Task,C,NC,Offer)
		<- !getScenario(A);
		    +startRescueMission(A,C,NC).
 
+!getScenario(D) <- .send(D,askAll,location(_,_,_)).
// Handling a Reject message
@r2 +!reject_proposal(CNPId)
		<- .print("I lost CNP ",CNPId, ".");
		// clear memory
		-proposal(CNPId,_,_).
