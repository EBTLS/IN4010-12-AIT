package ai2020.group29;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import geniusweb.actions.Accept;
import geniusweb.actions.Action;
import geniusweb.actions.Offer;
import geniusweb.actions.PartyId;
import geniusweb.actions.Vote;
import geniusweb.actions.Votes;
import geniusweb.bidspace.AllPartialBidsList;
import geniusweb.inform.ActionDone;
import geniusweb.inform.Finished;
import geniusweb.inform.Inform;
import geniusweb.inform.OptIn;
import geniusweb.inform.Settings;
import geniusweb.inform.Voting;
import geniusweb.inform.YourTurn;
import geniusweb.issuevalue.Bid;
import geniusweb.issuevalue.Value;
import geniusweb.party.Capabilities;
import geniusweb.party.DefaultParty;
import geniusweb.profile.PartialOrdering;
import geniusweb.profile.Profile;
import geniusweb.profile.utilityspace.LinearAdditive;
import geniusweb.profile.utilityspace.LinearAdditiveUtilitySpace;
import geniusweb.profile.utilityspace.UtilitySpace;
import geniusweb.profileconnection.ProfileConnectionFactory;
import geniusweb.profileconnection.ProfileInterface;
import geniusweb.progress.Progress;
import geniusweb.progress.ProgressRounds;
import tudelft.utilities.logging.Reporter;
//import geniusweb.voting.CollectedVotes;

/**
 * A simple party that places random bids and accepts when it receives an offer
 * with sufficient utility.
 * <h2>parameters</h2>
 * <table >
 * <caption>parameters</caption>
 * <tr>
 * <td>minPower</td>
 * <td>This value is used as minPower for placed {@link Vote}s. Default value is
 * 2.</td>
 * </tr>
 * <tr>
 * <td>maxPower</td>
 * <td>This value is used as maxPower for placed {@link Vote}s. Default value is
 * infinity.</td>
 * </tr>
 * </table>
 */
public class Group29_Main extends DefaultParty {

	private Bid lastReceivedBid = null;
	private PartyId me;
	private final Random random = new Random();
	protected ProfileInterface profileint;
	private Progress progress;
	private Settings settings;
	private Votes lastvotes;
	private String protocol;
	private int round = 0;
	
	private LinearAdditive linear_utilityAdditive;

	//issue name list of this domain
	Set<String> issue_name_list;
	
	// the threshold to vote a accept
	private float utility_threshold=(float)0.7;
	//the discount speed of threshold
	private float threshold_discount=(float)0.95;
	
	//threshold low bound
	private float threshold_lown_bound=(float)0.3;
	
	private HashMap< Integer, Bid> bids = new HashMap< Integer, Bid>();
	private HashMap< PartyId, Integer> idMap = new HashMap< PartyId, Integer>();
	private HashMap< Integer, PartyId> indexMap = new HashMap< Integer, PartyId>();
	private HashMap<String, Integer> issueMap =new HashMap<String,Integer>();
	private HashMap<String, Integer> valueMap =new HashMap<String,Integer>();
	//weight of different issue
	private HashMap<String,BigDecimal> weights=new HashMap<String,BigDecimal>();
	
	
	private String[] issue_sorted;
	
	//agent's gussing of other agents
	private HashMap<PartyId, float[]> guessing_weight=new HashMap<PartyId,float[]>();
	private HashMap<PartyId, int[]> changes_history=new HashMap<PartyId,int[]>();
	
	//other agent's previous bid
	private HashMap<PartyId, Bid> previous_bids=new HashMap<PartyId,Bid>();
	
	//other agent's current bid
	private HashMap<PartyId, Bid> current_bids = new HashMap<PartyId, Bid>();
	private float guessing_value[];
	private int important_issue[];
	private int precious_value[];
	private Bid my_bid;
	
	//power of different party
	private int power[];
	private int pleasure[];
	private int deleted[];
	// total number of agents
	int partyNumber;
	
	//Votes in each round
	List<Votes> currentVotes;

	//total number of issue
	int issueNumber;
	private int valueNumber = 0;
	PartyId myId;
	
	//flag whether it is first round
	int first_round=1;	


	public Group29_Main() {
	}

	public Group29_Main(Reporter reporter) {
		super(reporter); // for debugging
	}

	@Override
	public void notifyChange(Inform info) {
		try {
			if (info instanceof Settings) {
				Settings settings = (Settings) info;
				this.profileint = ProfileConnectionFactory
						.create(settings.getProfile().getURI(), getReporter());
				this.me = settings.getID();
				this.progress = settings.getProgress();
				this.settings = settings;
				this.protocol = settings.getProtocol().getURI().getPath();
				
				myId=settings.getID();
				getReporter().log(Level.INFO, "my_ID"+myId);

				//obtain issue numbers
				issueNumber=profileint.getProfile().getDomain().getIssues().size();
				getReporter().log(Level.INFO, "issueNumber:"+issueNumber);
				
				
				//obtain issue weights
				issue_name_list=profileint.getProfile().getDomain().getIssues();
				
				
				Integer i=0;
				for (String str: issue_name_list)
				{
					weights.put(str,((LinearAdditiveUtilitySpace) profileint.getProfile()).getWeight(str));
					for(int j=0;j< profileint.getProfile().getDomain().getValues(str).size().intValue();j++)
					{
						String s = profileint.getProfile().getDomain().getValues(str).get(j).toString();
						if (valueMap.get(s)==null)
						{
							valueMap.put(s,valueNumber++);
						}
					}
					if (issueMap.get(str)==null)
					{
						issueMap.put(str, i);
						i=i+1;
					}
					else
					{
						;
					}
					
				}
				// sorted my issue weights
					
				//update threshold
				if (utility_threshold>=threshold_lown_bound)
				{
					utility_threshold=utility_threshold*threshold_discount;
				}			
				
			} 
			else if (info instanceof ActionDone) {
				Action otheract = ((ActionDone) info).getAction();
				if (otheract instanceof Offer) {
					lastReceivedBid = ((Offer) otheract).getBid();
				}
			} 
			else if (info instanceof YourTurn) {
				getReporter().log(Level.INFO, "ROUND:"+round);
				makeOffer();
				getReporter().log(Level.INFO, "myturn over");
			} 
			else if (info instanceof Finished) {
				getReporter().log(Level.INFO, "Final ourcome"+round+":" + info);
			} 
			else if (info instanceof Voting) {
				getReporter().log(Level.INFO, "Votes start");
				String u = "";
				partyNumber=((Voting)info).getBids().size();
				for(int i=0;i<partyNumber;i++)
				{
					u+=((UtilitySpace) profileint.getProfile()).getUtility(((Voting)info).getBids().get(i).getBid()).doubleValue();
					u+=",";
				}
				getReporter().log(Level.INFO,( (Voting)info).toString());
				getReporter().log(Level.INFO, "vote start "+round+":"+u);
				u = "";
				for(int i=0;i<partyNumber;i++)
				{
					if(round>2)
						u+=pleasure[i];
					u+=",";
				}
				getReporter().log(Level.INFO, "vote start "+round+":"+u);
				for (String str:issue_name_list)
				{
					valueNumber = Math.max(valueNumber, profileint.getProfile().getDomain().getValues(str).size().intValue());
				}
				//getReporter().log(Level.INFO, "party_number"+partyNumber);
				if(round == 0)
				{
					this.power=new int[partyNumber];
					this.pleasure=new int[partyNumber];
					this.deleted=new int[partyNumber];
					this.guessing_value = new float[partyNumber*issueNumber*valueNumber];
					for(int i=0;i<partyNumber;i++)
						pleasure[i] = 10;
					for(int i=0;i<partyNumber*issueNumber*valueNumber;i++)
						guessing_value[i] = 0;
				}
				//initialize power map and id map
				for(int i=0;i<partyNumber;i++)
				{
					int index = -1;
					PartyId id = ((Voting) info).getBids().get(i).getActor();
//					getReporter().log(Level.INFO, "ID"+id.toString());
					Map<PartyId, Integer> powerMap = ((Voting) info).getPowers();
//					getReporter().log(Level.INFO, ((Voting) info).getPowers().toString());

					if(idMap.get(id)==null)
					{
						idMap.put(id, i);
						indexMap.put(i, id);
						power[i] = powerMap.get(id);
					}
					else
					{
						index = idMap.get(id);
						power[index] = powerMap.get(id);
					}
				}
				round++;
				if (current_bids.isEmpty()) 
				{
					//most important item for each agent
					important_issue=new int[partyNumber];
					float first_weights[]=new  float[issueNumber];
					int change_numbers[]=new int[issueNumber];
					for (int i=0;i<=issueNumber-1;i++)
					{
						first_weights[i]=(float)1/issueNumber;
						change_numbers[i]=0;
					}
//					getReporter().log(Level.INFO, "first_weights"+first_weights.toString());	
					
					//initialize
					for (int i=0;i<=partyNumber-1;i++)
					{
						
						PartyId id = ((Voting) info).getBids().get(i).getActor();
						
						//initialize the most important issue for other agents
						important_issue[i]=1;
						PartyId party_id=((Voting) info).getBids().get(i).getActor();
						
						//intialize guessing list, and changes history 
						guessing_weight.put(party_id, first_weights);
//						getReporter().log(Level.INFO, "first_weights"+first_weights.toString());						
						changes_history.put(party_id,change_numbers);
						current_bids.put(id, ((Voting)info).getBids().get(i).getBid());
						previous_bids.put(id, ((Voting)info).getBids().get(i).getBid());
					}
				}
				else {
					for (int i=0;i<=partyNumber-1;i++)
					{
						PartyId id = ((Voting) info).getBids().get(i).getActor();
						current_bids.replace(id, ((Voting)info).getBids().get(i).getBid());
						
						//update the guessing of other agents' weight					
						update_other_weights(id, current_bids.get(id));
						previous_bids.replace(id, ((Voting)info).getBids().get(i).getBid());
						
					}
				}
					lastvotes = agent_vote((Voting) info);
					getReporter().log(Level.INFO, "Votes End");
					getConnection().send(lastvotes);
			}
				else if (info instanceof OptIn) {
				
				getReporter().log(Level.INFO, "start OptIn");
					
				currentVotes = ((OptIn) info).getVotes();
				for(int i=0;i<partyNumber;i++)
				{
					int index =  idMap.get(currentVotes.get(i).getActor());
					if(currentVotes.get(i).getVote(my_bid)==null&&deleted[index]==1)
						pleasure[index]--;
					else if(currentVotes.get(i).getVote(my_bid)!=null)
						pleasure[index] = Math.max(pleasure[index]+1, 1);
				}
				lastvotes=opt_vote((OptIn) info);
				getConnection().send(lastvotes);
			}
		} 
			catch (Exception e) {
			throw new RuntimeException("Failed to handle info", e);
		}
		updateRound(info);
	}

	@Override
	public Capabilities getCapabilities() {
		return new Capabilities(
				new HashSet<>(Arrays.asList("SAOP", "AMOP", "MOPAC")),
				Collections.singleton(Profile.class));
	}


	public String getDescription() {
		return "places random bids until it can accept an offer with utility >0.6. "
				+ "Parameters minPower and maxPower can be used to control voting behaviour.";
	}

	/**
	 * Update {@link #progress}
	 * 
	 * @param info the received info. Used to determine if this is the last info
	 *             of the round
	 */
	private void updateRound(Inform info) {
		if (protocol == null)
			return;
		switch (protocol) {
		case "SAOP":
		case "SHAOP":
			if (!(info instanceof YourTurn))
				return;
			break;
		case "MOPAC":
			if (!(info instanceof OptIn))
				return;
			break;
		default:
			return;
		}
		// if we get here, round must be increased.
		if (progress instanceof ProgressRounds) {
			progress = ((ProgressRounds) progress).advance();
		}

	}

	/**
	 * send our next offer
	 */
	private void makeOffer() throws IOException {
		Action action;
		my_bid=null;
		Bid my_besBid=null;
		if ((protocol.equals("SAOP") || protocol.equals("SHAOP"))
				&& isGood(lastReceivedBid)) {
			action = new Accept(me, lastReceivedBid);
		}
		else {
			if(first_round==1)
			{
				my_bid=makeFirstBid();
				my_besBid=my_bid;
				first_round = first_round-1;
			}
			else {
				if(round<30)
					my_bid=updateBid(my_bid,1);
				else my_bid=updateBid(my_bid,2);
//				my_bid=makeFirstBid();
			}
			
			action = new Offer(me, my_bid);
		}
		getConnection().send(action);

	}

	private Bid makeFirstBid() throws IOException{

		HashMap< String, Value> issueValues = new HashMap< String, Value>();
//		AllBidsList bidsList=new AllBidsList(profileint.getProfile().getDomain());
		String issuename;
		Bid my_bid=null;
		
		//first time total greedy issue
//		for (int i=0;i<issueNumber;i++)
//		{
			Value issue_value;
			
			//for each issue find the max utility
			for (String str:issue_name_list)
			{
				issuename=str;
				issue_value=profileint.getProfile().getDomain().getValues(issuename).get((long)0);
				BigDecimal issue_max_utility=((LinearAdditiveUtilitySpace) profileint.getProfile()).getUtilities().get(issuename).getUtility(issue_value);
				
				//对于每个issue，遍历value
				for (int j=0;j<=profileint.getProfile().getDomain().getValues(issuename).size().intValue()-1;j++)
				{
					Value temp_issue_Value=profileint.getProfile().getDomain().getValues(issuename).get((long)j);
					BigDecimal temp_utility=((LinearAdditiveUtilitySpace) profileint.getProfile()).getUtilities().get(issuename).getUtility(temp_issue_Value);
					
					//更新最大value
					if (temp_utility.compareTo(issue_max_utility)==1)
					{
						issue_max_utility=temp_utility;
						issue_value=temp_issue_Value;
					}
					
				}
			issueValues.put(issuename,issue_value);
			}
//		}
		my_bid=new Bid(issueValues);
		//getReporter().log(Level.INFO, "my_bid"+my_bid.toString());
		return my_bid;

	}

	
	private Bid updateBid(Bid previousBid,int strategy) throws IOException {
		
		HashMap< String, Value> issueValues = new HashMap< String, Value>();
//		AllBidsList bidsList=new AllBidsList(profileint.getProfile().getDomain());
		Bid temp_Bid=previousBid;
//		issue_name_list=profileint.getProfile().getDomain().getIssues();
		
		
		//change the not utility one 
		if (strategy==1)
		{
			int index=(int) Math.floor(Math.random()*(Math.floor(issue_name_list.size()/2)));
			//getReporter().log(Level.INFO, "index"+index);
			String issue_change=find_issuename(false, index);
			
			Value issue_value;
			
			//for each issue find the max utility

			for (String str:issue_name_list)
			{
				issue_value=profileint.getProfile().getDomain().getValues(str).get((long)0);
				BigDecimal issue_max_utility=((LinearAdditiveUtilitySpace) profileint.getProfile()).getUtilities().get(str).getUtility(issue_value);
				if (str!=issue_change)
				{
					for (int j=0;j<=profileint.getProfile().getDomain().getValues(str).size().intValue()-1;j++)
					{
						Value temp_issue_Value=profileint.getProfile().getDomain().getValues(str).get((long)j);
						BigDecimal temp_utility=((LinearAdditiveUtilitySpace) profileint.getProfile()).getUtilities().get(str).getUtility(temp_issue_Value);
						if (temp_utility.compareTo(issue_max_utility)==1)
						{
							issue_max_utility=temp_utility;
							issue_value=temp_issue_Value;
						}	
					}
				}
				else 
				{
					for (int j=0;j<profileint.getProfile().getDomain().getValues(str).size().intValue();j++)
					{
						Value temp_issue_Value=profileint.getProfile().getDomain().getValues(str).get((long)j);
						BigDecimal temp_utility=((LinearAdditiveUtilitySpace) profileint.getProfile()).getUtilities().get(str).getUtility(temp_issue_Value);
						if (Math.random()>0.5)
						{
							issue_value=temp_issue_Value;
						}
					}
				}
				issueValues.put(str,issue_value);
			}
		}
		
		//concede to other agents
		else if (strategy==2) 
		{
			int maxPower = 0;
			for(int i=0;i<partyNumber;i++)
			{
				deleted[i] = 1;
				maxPower = Math.max(maxPower, power[i]);
			}
			
			//optimize 1
			for(int i=0;i<partyNumber;i++)
			{
				if(pleasure[i]<0)
				{
					if (Math.random()<(power[i]/maxPower)/2)
					{
						pleasure[i] = 0;
					}
					else
					{
						deleted[i] = 0;
					}
				}
			}
			//end
			
			for (String str:issue_name_list)
			{
				Value issue_value=profileint.getProfile().getDomain().getValues(str).get((long)0);
				int index=issueMap.get(str);
				double ma = -1;
				int resultValue = -1;
				for (int i=0;i<profileint.getProfile().getDomain().getValues(str).size().intValue();i++)
				{
					double sum = 0;
					for(int j=0;j<partyNumber;j++)
					{
						PartyId id = indexMap.get(j);
						if(!id.equals(me))
							sum+=guessing_value[j*issueNumber*valueNumber+index*issueNumber+i]/round*power[j]*deleted[j];
					}
					if(Math.random()<weights.get(str).doubleValue()||round<60)
					{
						Value temp_issue_Value=profileint.getProfile().getDomain().getValues(str).get((long)i);
						double temp_utility=((LinearAdditiveUtilitySpace) profileint.getProfile()).getUtilities().get(str).getUtility(temp_issue_Value).doubleValue();
						sum += temp_utility*maxPower*2;
					}
					if(sum>ma)
					{
						ma = sum;
						resultValue = i;
					}
				}
				issue_value=profileint.getProfile().getDomain().getValues(str).get((long)resultValue);
				issueValues.put(str,issue_value);
			}
		}
		
		
		Bid my_bid=new Bid(issueValues);
		
		
		return my_bid;
		
	}
	
	
	
	/**
	 * @param bid the bid to check
	 * @return true iff bid is good for us.
	 */
	private boolean isGood(Bid bid) {
		if (bid == null)
			return false;
		Profile profile;
		try {
			profile = profileint.getProfile();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		if (profile instanceof UtilitySpace)
		{
			if(round<30)
				return ((UtilitySpace) profile).getUtility(bid).doubleValue() > 0.90;
			else return ((UtilitySpace) profile).getUtility(bid).doubleValue() > 0.75;
		}
		if (profile instanceof PartialOrdering) {
			return ((PartialOrdering) profile).isPreferredOrEqual(bid,
					profile.getReservationBid());
		}
		return false;
	}

	
	
	private Votes opt_vote(OptIn voting) throws IOException{
		
		Set<Vote> votes=new HashSet<Vote>();
		
		Set<Vote> my_vote_list;
		
		
//		getReporter().log(Level.INFO, "current_power"+power);
		
		my_vote_list=lastvotes.getVotes();
		getReporter().log(Level.INFO, "my_vote_list"+lastvotes.getVotes().toString());
		
		for (Vote my_vote: my_vote_list)
		{
			
			int accept_number=0; 
			
			int current_Power=0;
			
			Bid temp_bid=my_vote.getBid();
			

			//the goodness
			if (good_level(temp_bid)==1 || good_level(temp_bid)==2)
			{
				
				//the attitude of other parties
				for(int i=0;i<partyNumber;i++)
				{
					int index =  idMap.get(currentVotes.get(i).getActor());
					if (currentVotes.get(i).getVote(temp_bid)!=null)
					{
						accept_number=accept_number+1;
						current_Power=current_Power+power[index];
					}
					
				}
//				getReporter().log(Level.INFO, "current_power"+power);
				
				
				if (current_Power>=lastvotes.getVote(temp_bid).getMinPower()
						&& current_Power<lastvotes.getVote(temp_bid).getMaxPower()
						&& good_level(temp_bid)==1)
				{
					int min_Power=current_Power+1;
					int max_Power=lastvotes.getVote(temp_bid).getMaxPower();
					Vote temp_vote=new Vote(myId, temp_bid, min_Power, max_Power);
					votes.add(temp_vote);
				}
				
				//reduce acceptance
				else if (current_Power>=lastvotes.getVote(temp_bid).getMinPower()
						&& current_Power<lastvotes.getVote(temp_bid).getMaxPower()
						&& good_level(temp_bid)==2) 
				{
					int temp_min_Power=lastvotes.getVote(temp_bid).getMinPower();
					int max_Power=lastvotes.getVote(temp_bid).getMaxPower();
					int min_Power=(int) Math.floor((max_Power+temp_min_Power)/2);
					Vote temp_vote=new Vote(myId, temp_bid, min_Power, max_Power);
					votes.add(temp_vote);
					
				}
			}
			

			else 
			{
				int min_Power=lastvotes.getVote(temp_bid).getMinPower();
				int max_Power=lastvotes.getVote(temp_bid).getMaxPower();
				Vote temp_vote=new Vote(myId, temp_bid, min_Power, max_Power);
				votes.add(temp_vote);
				
			}
			
			
		}	
		
		
		return new Votes(me,votes);
		
	}
	
	// calculate minpower, maxpower based on percentage and sequence
	// sequence: max power / min power
	// percentage: percentage
	// sorted_power: a int[] sorted from smallest to largest
	private int sum_power(int sequence,int[] sorted_power,double percentage) {
		
		int power=0;
		
		int length=sorted_power.length;
		
		int number_of_party= (int) Math.floor(length*percentage);
		
		
		if (sequence==0) {
			for (int i=0;i<=number_of_party;i++) {
				power=power+sorted_power[i];
			}
		}
		else {
			for (int i=length-1;i>=length-1-number_of_party;i--) {
				power=power+sorted_power[i];
			}

		}
				
		return power;		
		
	}
	
	
	//judge a bid is how good to the agent
	private int good_level(Bid bid) {
		if (bid == null)
			return -1;
		Profile profile;
		try {
			profile = profileint.getProfile();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}


		if (profile instanceof UtilitySpace)
			if (((UtilitySpace) profile).getUtility(bid).doubleValue() >= 4*(1-utility_threshold)/5+utility_threshold) {
				return 5;
			}
			else if (((UtilitySpace) profile).getUtility(bid).doubleValue() >= 3*(1-utility_threshold)/5+utility_threshold){
				return 4;
			}
			else if (((UtilitySpace) profile).getUtility(bid).doubleValue() >= 2*(1-utility_threshold)/5+utility_threshold) {
				return 3;
			}
			else if (((UtilitySpace) profile).getUtility(bid).doubleValue() >= 1*(1-utility_threshold)/5+utility_threshold) {
				return 2;
			}
			else if (((UtilitySpace) profile).getUtility(bid).doubleValue() > utility_threshold) {
				return 1;
			}
			else {
				return 0;
			}
		if (profile instanceof PartialOrdering) {
			if(((PartialOrdering) profile).isPreferredOrEqual(bid,profile.getReservationBid()))
			{
				return 1;
			}
		}
		return -1;
	}
	


	//agents' action in voting phase
	private Votes agent_vote(Voting voting) throws IOException {
		
		Integer minPower[]=new Integer[5];
		Integer maxPower[]=new Integer[5];
		double percentage_min[]= {0,0.3,0.4,0.5,0.7};
		double percentage_max[]= {0,0.7,0.6,0.5,0.3};
		
		int sorted_power[]= power;
		//getReporter().log(Level.INFO, "power_length"+power.length);
//		int sorted_power[]= {1,3,5,4,2};
		Arrays.sort(sorted_power);	
		
		minPower[0]=1;
		maxPower[0]=Integer.MAX_VALUE;
		

		//initialize 
		for (int i=1;i<=4;i++) {
			
			minPower[i]=sum_power(0, sorted_power, percentage_min[i]);
			maxPower[i]=sum_power(1, sorted_power,percentage_max[i]);
			getReporter().log(Level.INFO, "min:" + minPower[i]);
			getReporter().log(Level.INFO, "max:" + maxPower[i]);
		}
		Set<Vote> votes;
		Set<Vote> temp_votes;	
		
		votes = voting.getBids().stream().distinct()
				.filter(offer -> good_level((offer.getBid()))==5)
				.map(offer -> new Vote(me, offer.getBid(), minPower[0], maxPower[0]))
				.collect(Collectors.toSet());
		getReporter().log(Level.INFO, "temp_votes5"+votes.toString());

		// max_power =sum of 50% most powerful man, min_power=sum of 50% min powerful man		
		temp_votes = voting.getBids().stream().distinct()
				.filter(offer -> good_level((offer.getBid()))==4)
				.map(offer -> new Vote(me, offer.getBid(), minPower[1], maxPower[1]))
				.collect(Collectors.toSet());
		getReporter().log(Level.INFO, "temp_votes4"+temp_votes.toString());
		votes.addAll(temp_votes);

		temp_votes = voting.getBids().stream().distinct()
				.filter(offer -> good_level((offer.getBid()))==3)
				.map(offer -> new Vote(me, offer.getBid(), minPower[2], maxPower[2]))
				.collect(Collectors.toSet());
		votes.addAll(temp_votes);
		getReporter().log(Level.INFO, "temp_votes3"+temp_votes.toString());
		
		temp_votes = voting.getBids().stream().distinct()
				.filter(offer -> good_level((offer.getBid()))==2)
				.map(offer -> new Vote(me, offer.getBid(), minPower[3], maxPower[3]))
				.collect(Collectors.toSet());
		votes.addAll(temp_votes);
		//getReporter().log(Level.INFO, "temp_votes2"+temp_votes.toString());
		
		if (minPower[4]<=maxPower[4]) {
			votes = voting.getBids().stream().distinct()
					.filter(offer -> good_level((offer.getBid()))==1)
					.map(offer -> new Vote(me, offer.getBid(), minPower[4], maxPower[4]))
					.collect(Collectors.toSet());	
			votes.addAll(temp_votes);
//			getReporter().log(Level.INFO, "Haha:" + minPower[0]);
		}
		return new Votes(me,votes);
	}
	
	
	//update agent's guessing of other agent's weight
	private void update_other_weights(PartyId partyId,Bid currentBid) {
		
		float[] temp_weight=guessing_weight.get(partyId);
		int[] change_numbers=changes_history.get(partyId);
		Bid previousBid=previous_bids.get(partyId);
		
		
		for (String str: issue_name_list)
		{
			int i=issueMap.get(str).intValue();
			//if issue i change one more time, records it
			//getReporter().log(Level.INFO, "Haha:" + partyId);
			guessing_value[idMap.get(partyId)*issueNumber*valueNumber+i*valueNumber+valueMap.get(currentBid.getValue(str).toString())]+=1;
			if (currentBid.getValue(str).equals(previousBid.getValue(str))==false)
			{
				change_numbers[i]=change_numbers[i]+1;
			}
		}
		changes_history.replace(partyId, change_numbers);
		
		float sum=0;
		
		// constant/change_number
		
		for (int i=0;i<=temp_weight.length-1;i++)
		{
			temp_weight[i]=(float) 1/(change_numbers[i]+1);
			sum=sum+temp_weight[i];
		}
		
		//normalize
		for (int i=0;i<=temp_weight.length-1;i++)
		{
			temp_weight[i]=(float) temp_weight[i]/sum;
		}
		
		guessing_weight.replace(partyId, temp_weight);
	}
	
	
	// find the index th max/min issuename of agent id from guessing_weight
	/* sequBoolean:true for max, false for min 
	 */
	private String find_issuename(HashMap<PartyId, float[]> guessing_weight,PartyId id, Boolean sequBoolean,int index) {
		
		String nameString=null;
		
		float weights_float[]=guessing_weight.get(id);
		
		Arrays.sort(weights_float);
		
		if (sequBoolean)
		{
			for (String str:issue_name_list)
			{
				int i=idMap.get(str).intValue();
				if ((guessing_weight.get(id))[i]==weights_float[guessing_weight.get(id).length-1-index])
				{
					nameString=str;
					break;
				}
			}
		}
		else 
		{
			for (String str:issue_name_list)
			{
				int i=idMap.get(str).intValue();
				if ((guessing_weight.get(id))[i]==weights_float[index])
				{
					nameString=str;
					break;
				}
			}
		}
		return nameString;
	}
	
	// find the index th max/min issuename of our agent
	/* sequBoolean:true for max, false for min 
	 */
	private String find_issuename(Boolean sequBoolean,int index) {
		
		String nameString=null;
		
		BigDecimal weights_decimal[]=new BigDecimal[issue_name_list.size()]; 
		
		
		for (String str: issue_name_list)
		{
			int i=issueMap.get(str).intValue();
			weights_decimal[i]=weights.get(str);
//			getReporter().log(Level.INFO, "weights"+i+"decimal"+weights_decimal[i]);
		}
	
		
		Arrays.sort(weights_decimal);
		
		if (sequBoolean)
		{
			for (String str:issue_name_list)
			{
				if (weights.get(str)==weights_decimal[weights.size()-index])
				{
					nameString=str;
					break;
				}
			}
		}
		else 
		{
			for (String str:issue_name_list)
			{
				if (weights.get(str)==weights_decimal[index])
				{
					nameString=str;
					break;
				}
			}
		}
		return nameString;
	}

}
