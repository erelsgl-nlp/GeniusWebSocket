package negotiator;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import negotiator.exceptions.NegotiatorException;
import negotiator.exceptions.Warning;
import negotiator.issue.ISSUETYPE;
import negotiator.issue.Issue;
import negotiator.issue.IssueDiscrete;
import negotiator.issue.IssueInteger;
import negotiator.issue.IssueReal;
import negotiator.issue.Objective;
import negotiator.issue.Value;
import negotiator.issue.ValueDiscrete;
import negotiator.issue.ValueInteger;
import negotiator.issue.ValueReal;
import negotiator.utility.UtilitySpace;
import negotiator.xml.SimpleDOMParser;
import negotiator.xml.SimpleElement;

/**
 * <p>Domain is a general subject about which agents can negotiate.
 * <p>Each domain contains one or more {@link Objective}s, 
 * and each Objective contains one or more {@link Issue}s.
 * <p>A Domain is usually read from an XML file.
 * <p>An example domain is "Job Candidate", which includes
 * a single objective with several issues "salary", "work hours", "company car", and more.
 *
 * @author Dmytro Tykhonov & Koen Hindriks
 * @author W.Pasman: Numerous modifications 
 * @author erelsgl: natural language support
 */
public class Domain {
	
	// Map side-name (e.g. "employer" or "candidate") to a map from a personality (e.g. "compromise", "long-term") to a filename with the utility space.
	private Map<String, Map<String,String>> mapSideNameToUtilitySpace = new HashMap<String, Map<String,String>>();
	
	// reverse map: map utility space file name to the corresponding side name (e.g. "employer", "candidate").
	private Map<String, String> mapUtilitySpaceToSideName = new HashMap<String, String>();

	// reverse map: map utility space file name to the corresponding personality (e.g. short-term, long-term).
	private Map<String, String> mapUtilitySpaceToPersonality = new HashMap<String, String>();

	// Map side-name (e.g. "employer" or "candidate") to a filename with the NLP data.
	private Map<String, String> mapSideNameToNLPData = new HashMap<String, String>();

	private Map<String, Issue> issuesByName = new HashMap<String, Issue>();
	private Map<String, String> issuesByValue = new HashMap<String, String>();

    private Objective fObjectivesRoot;
    private String pathToDataFiles;
    private String domainName;
    private SimpleElement root;
    public Domain(){
    	fObjectivesRoot = null;
    	pathToDataFiles="";
    	domainName="";
    }
    public SimpleElement getXMLRoot() {
    	return root;
    }
    
    public Domain(SimpleElement root) {
    	this.root = root;
    	loadTreeFromXML(root, /*filename=*/null);
    }
    
    public Domain(String filename) throws IOException {    	
		this(filename, new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename)), "UTF8")));
    }
    
    public Domain(File filename) throws IOException    {
    		this(filename.getAbsolutePath(), new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF8")));
    }
    
    public Domain(String fileName, Reader reader) throws IOException {
    	File file = new File(fileName);
    	String pathToOriginalXmlFile = file.getAbsolutePath();

        int i = pathToOriginalXmlFile.lastIndexOf(File.separator);
        pathToDataFiles = (i > -1) ? pathToOriginalXmlFile.substring(0, i) : pathToOriginalXmlFile;
        
        domainName = pathToOriginalXmlFile;
        
		root = new SimpleDOMParser().parse(reader);
		
		SimpleElement xml_utility_space;
		try { 
			xml_utility_space = root.getChildByTagName("utility_space")[0]; 
		}
		catch (Exception err) 
		{ throw new IOException("Can't read from file, incorrect format of file"); }
		loadTreeFromXML(xml_utility_space, pathToOriginalXmlFile);

		for (Issue issue: getIssues()) {
			issuesByName.put(issue.toString(), issue);
			if (issue instanceof IssueDiscrete) {
				for (ValueDiscrete value: ((IssueDiscrete)issue).getValues()) {
					issuesByValue.put(value.toString().toLowerCase(), issue.toString());
				}
			}
		}
    	
    }

	
	/**
	 * Get the owner name of the given utility-space file. 
	 * This is the "owner" attribute of the "agent" element in the domain XML file.
	 * @param fileName name of a file of a utility-space for this domain.
	 * @return name of the owner of this domain, e.g. "employer", "candidate".
	 */
	public String getSideName(String fileName) {
		fileName = fileName.replaceAll("^.*/", "");
		String sideName = mapUtilitySpaceToSideName.get(fileName);
		if (sideName==null)
			throw new IllegalArgumentException("Cannot find the owner for the utility-space '"+fileName+"'. map="+mapUtilitySpaceToSideName+". Make sure that the domain XML file has an agent element with that utility_space attribute and an owner attribute.");
		return sideName;
	}
	
	/**
	 * Get the owner name of the given utility-space file. 
	 * This is the "owner" attribute of the "agent" element in the domain XML file.
	 * @param fileName name of a file of a utility-space for this domain.
	 * @return name of the owner of this domain, e.g. "employer", "candidate".
	 */
	public String getPersonality(String fileName) {
		fileName = fileName.replaceAll("^.*/", "");
		String sideName = mapUtilitySpaceToPersonality.get(fileName);
		if (sideName==null)
			throw new IllegalArgumentException("Cannot find the personality for the utility-space '"+fileName+"'. map="+mapUtilitySpaceToPersonality+". Make sure that the domain XML file has an agent element with that utility_space attribute and an owner attribute.");
		return sideName;
	}

    /**
     * Returns an issue with a given index. Considers issues in the domain tree as a plain array (uses getIssues method to generate the array).
     * 
     * Wouter: Warning, getIssue does NOT get issue with ID index, the name is WRONG
     * A better name would be getChild 
     * 
     * @deprecated Use getObjective
     * 
     * @param index - index of the issue
     * @return
     */
    @Deprecated public final Objective getIssue(int index) {
    	return getIssues().get(index);
    }
    
   
     /**
      * @param ID (number) of the objective
      * @return the objective with given ID
      */
    public final Objective getObjective(int ID){
    	return fObjectivesRoot.getObjective(ID); 
    }
    
    public final Objective getObjectivesRoot(){
    	return fObjectivesRoot; //TODO hdevos this could be done in a more elegant way. To discuss with Richard.
    }   

    /**
     * Sets a new domain root.
     * @param ob The new root Objective
     */
    public final void setObjectivesRoot(Objective ob){
    	fObjectivesRoot = ob;
    }
    
    /**
     * @author Herbert
     * @param domainRoot The SimpleElement that contains the root of the Objective tree.
     * @param domainFileName (optional) if not null, this is the domain file name. It is used only for exception messages.
     */
    private final void loadTreeFromXML(SimpleElement domainRoot, String domainFileName) {
    	//SimpleElement root contains a LinkedList with SimpleElements.
    	/*
    	 * Structure of the file:
    	 * 
    	 * pRoot contains information about how many items there exist in the utilityspace.
    	 * The first SimpleElement under pRoot contains the root objective of the tree, with a number of objective
    	 * as tagnames.
    	 */    	
		   
		// Collect available utility spaces for each of the sides:
		SimpleElement[] currentIssueAgentElements = domainRoot.getChildByTagName("agent");
		if (currentIssueAgentElements!=null && currentIssueAgentElements.length>0) {
		    for (SimpleElement currentAgentElement: currentIssueAgentElements) {
		    	String utility_space = currentAgentElement.getAttribute("utility_space");
		    	String owner = currentAgentElement.getAttribute("owner");
		    	String personality = currentAgentElement.getAttribute("personality");
		    	
		    	if (mapSideNameToUtilitySpace.get(owner)==null)
		    		mapSideNameToUtilitySpace.put(owner, new HashMap<String,String>());
		    	mapSideNameToUtilitySpace.get(owner).put(personality, utility_space);
		    	mapUtilitySpaceToSideName.put(utility_space, owner);
		    	mapUtilitySpaceToPersonality.put(utility_space, personality);
		    }
		} else {
			//System.out.println("root="+pRoot);
			throw new IllegalArgumentException("No agent elements in domain file '"+domainFileName+"'");
		}
		   
		// Collect available NLP data for each of the sides:
		SimpleElement[] currentIssueNLPElements = domainRoot.getChildByTagName("nlp");
		if (currentIssueNLPElements!=null && currentIssueNLPElements.length>0) {
		    for (SimpleElement currentNLPElement: currentIssueNLPElements) {
		    	String filename = currentNLPElement.getAttribute("name");
		    	String owner = currentNLPElement.getAttribute("owner");
		    	mapSideNameToNLPData.put(owner, filename);
		    }
		}

    	SimpleElement rootElement = (domainRoot.getChildByTagName("objective")[0]); //Get the actual root Objective. 
    	int rootIndex = Integer.valueOf(rootElement.getAttribute("index"));

        Objective objAlmostRoot = new Objective();
        objAlmostRoot.setNumber(rootIndex);
        String name = rootElement.getAttribute("name");
       	objAlmostRoot.setName(name==null? "root": name);
        
        fObjectivesRoot = buildTreeRecursive(rootElement, objAlmostRoot);
    }
    
    
    
    //added by Herbert
    /**
     * 
     * @param currentLevelRoot The current SimpleElement containing the information for the Objective on this level.
     * @param currentParent parent of the current level of this branch of the tree.
     * @return The current parent of this level of the tree, with the children attached.
     */
    private final Objective buildTreeRecursive(SimpleElement currentLevelRoot, Objective currentParent){
    	Object[] currentLevelObjectives = currentLevelRoot.getChildByTagName("objective");
    	for (int i =0; i < currentLevelObjectives.length; i++){
       			SimpleElement childObjectives = (SimpleElement)currentLevelObjectives[i];
       			int obj_index = Integer.valueOf(childObjectives.getAttribute("index"));
    			Objective child = new Objective(currentParent);
    			child.setNumber(obj_index);
    			//Set child attributes based on childObjectives.
    			child.setName(childObjectives.getAttribute("name"));
    			currentParent.addChild(buildTreeRecursive(childObjectives, child));
    	}
    	
    	SimpleElement[] currentLevelIssues = currentLevelRoot.getChildByTagName("issue");
    	for(int j = 0; j < currentLevelIssues.length; j++) {
    		Issue currentLevelIssue = null;
    		
    		SimpleElement currentLevelIssueElement = currentLevelIssues[j]; // the issue element
    		String issueName = currentLevelIssueElement.getAttribute("name");
    		int index = Integer.parseInt(currentLevelIssueElement.getAttribute("index"));

            // Collect issue value type from XML file.
            String type = currentLevelIssueElement.getAttribute("type");
            String vtype = currentLevelIssueElement.getAttribute("vtype");
            ISSUETYPE issueType;
        	if (type==null) { // No value type specified.
        		new Warning("Type not specified in template file.");
            	issueType = ISSUETYPE.DISCRETE;
        	} else if (type.equals(vtype)) {
            	// Both "type" as well as "vtype" attribute, but consistent.
            		issueType = ISSUETYPE.convertToType(type);
            } else if (vtype!=null) {
            	issueType = ISSUETYPE.convertToType(vtype);
            } else { // Used label "type" instead of label "vtype".
            	issueType = ISSUETYPE.convertToType(type);
            }
            

//          Collect values and/or corresponding parameters for issue type.
            int nrOfItems, minI, maxI;
            double minR, maxR;
            String[] values;
            String[] desc;
            Double[] cost;
            switch(issueType) {
            case INTEGER:
            	// Collect range bounds for integer-valued issue from xml template
            	SimpleElement[] currentIssueRange = currentLevelIssueElement.getChildByTagName("range");
            	minI = Integer.valueOf(currentIssueRange[0].getAttribute("lowerbound"));
            	maxI = Integer.valueOf(currentIssueRange[0].getAttribute("upperbound"));
            	currentLevelIssue = new IssueInteger(issueName, index, minI, maxI, currentParent);
            	break;
            case REAL:
            	// Collect range bounds for integer-valued issue from xml template
            	currentIssueRange = currentLevelIssueElement.getChildByTagName("range");
            	minR = Double.valueOf(currentIssueRange[0].getAttribute("lowerbound"));
            	maxR = Double.valueOf(currentIssueRange[0].getAttribute("upperbound"));
            	currentLevelIssue = new IssueReal(issueName, index, minR, maxR);
            	break;
 // Issue values cannot be of type "price" anymore... TODO: Remove when everything works.
 //           case PRICE:
 //           	// Collect range bounds for integer-valued issue from xml template
 //           	currentIssueRange = childIssues.getChildByTagName("range");
 //           	minR = Integer.valueOf(((SimpleElement)xml_item).getAttribute("lowerbound"));
 //           	maxR = Integer.valueOf(((SimpleElement)xml_item).getAttribute("upperbound"));
 //           	issue = new IssuePrice(name, index, minR, maxR);
 //           	break;
            case DISCRETE:
            default: // By default, create discrete-valued issue
           	// Collect discrete values for discrete-valued issue from xml template            	
            	SimpleElement[] currentIssueValues = currentLevelIssueElement.getChildByTagName("item");
                nrOfItems = currentIssueValues.length;

                values = new String[nrOfItems];
                desc = new String[nrOfItems];
                cost = new Double[nrOfItems];

                for(int k=0;k<nrOfItems;k++) {
                	// TODO: check range of indexes.
                	SimpleElement currentIssueValueElement = currentIssueValues[k];
                    //item_index = Integer.valueOf(currentIssueValueElement.getAttribute("index"));
                    values[k] = currentIssueValueElement.getAttribute("value");
                    desc[k]=currentIssueValueElement.getAttribute("description");
                    if(currentIssueValueElement.getAttribute("cost")!=null)
                    	cost[k] = Double.valueOf(currentIssueValueElement.getAttribute("cost"));

            		// Create default natural language input patterns for the value:
            		//for (String owner: NLP.owners()) 
            		//	NLP.addImplicitInputPattern(owner, "(?i)I want "+values[k], issueName, values[k]);
                }
                currentLevelIssue = new IssueDiscrete(issueName, index, values, desc,currentParent, cost);
            	break;
            }
    		    		
 //Descriptions?   		child.setDescription(childIssues.getAttribute("description"));
    /*		Double weight = new Double(childIssues.getAttribute("weight"));
    		child.setWeight(weight.doubleValue());
    */		currentLevelIssue.setNumber(index);
    	
            try {
            	currentParent.addChild(currentLevelIssue);
            } catch(Exception e) {
            	System.out.println("child is NULL");
            	e.printStackTrace();
            }
       	}
    	
    	return currentParent;
    }
    
	/** KH 070511: Moved to here since it is generic method that can be made available to all agents.
	 * Wouter: NOTE, it is NOT checked whether the bid has a utility>0.
	 * @return a random bid
	 */
	public final Bid getRandomBid()
	{
		HashMap<Integer, Value> values = new HashMap<Integer, Value>();
		
       int lNrOfOptions, lOptionIndex;

       // For each issue, compute a random value to return in bid.
       for (Issue lIssue: getIssues()) {
			switch(lIssue.getType()) {
			case DISCRETE:
				IssueDiscrete lIssueDiscrete = (IssueDiscrete)lIssue;
	            lNrOfOptions =lIssueDiscrete.getNumberOfValues();
	            lOptionIndex = Double.valueOf(java.lang.Math.random()*(lNrOfOptions)).intValue();
	            if (lOptionIndex >= lNrOfOptions)
	            	lOptionIndex= lNrOfOptions-1;
				//values[i]= lIssueDiscrete.getValue(lOptionIndex);
	            values.put(lIssue.getNumber(), lIssueDiscrete.getValue(lOptionIndex));
				break;
			case INTEGER:
		        lNrOfOptions = ((IssueInteger)lIssue).getUpperBound()-((IssueInteger)lIssue).getLowerBound()+1;
		        lOptionIndex = Double.valueOf(java.lang.Math.random()*(lNrOfOptions)).intValue();
	            if (lOptionIndex >= lNrOfOptions)
	            	lOptionIndex= lNrOfOptions-1;
	            //values[i]= new ValueInteger(((IssueInteger)lIssue).getLowerBound()+lOptionIndex);
	            values.put(lIssue.getNumber(), new ValueInteger(((IssueInteger)lIssue).getLowerBound()+lOptionIndex));
		        break;
			case REAL:
				IssueReal lIssueReal =(IssueReal)lIssue;
				lNrOfOptions =lIssueReal.getNumberOfDiscretizationSteps();
				double lOneStep = (lIssueReal.getUpperBound()-lIssueReal.getLowerBound())/lNrOfOptions;
	            lOptionIndex = Double.valueOf(java.lang.Math.random()*(lNrOfOptions)).intValue();
	            if (lOptionIndex >= lNrOfOptions)
	            	lOptionIndex= lNrOfOptions-1;
				//values[i]= new ValueReal(lIssueReal.getLowerBound()+lOneStep*lOptionIndex);
	            values.put(lIssue.getNumber(), new ValueReal(lIssueReal.getLowerBound()+lOneStep*lOptionIndex));
				break;
			}
		}
       try { 
        return new Bid(this,values);
       }
       catch (Exception e) { System.out.println("problem getrandombid:"+e.getMessage()) ; }
       return null;
	}
	
	/**
	 * Creates an XML representation of this domain.
	 * @return the SimpleElements representation of this Domain or <code>null</code> when there was an error.
	 */
	public SimpleElement toXML(){
		SimpleElement root = new SimpleElement("utility_space");
		//set attributes for this domain
		root.setAttribute("number_of_issues", ""+0); //unknown right now
		root.addChildElement(fObjectivesRoot.toXML());
		return root;
	}
	
	/**
	 * @author W.Pasman
	 * @return all objectives (note, issues are also objectives!) in the domain
	 */
	public ArrayList<Objective> getObjectives()
	{
		Enumeration<Objective> objectives=fObjectivesRoot.getPreorderEnumeration();
		ArrayList<Objective> objectivelist=new ArrayList<Objective>();
		while (objectives.hasMoreElements()) objectivelist.add(objectives.nextElement());
		return objectivelist;
	}
	
	/**
	 * Get all issues as an arraylist.
	 * @author W.Pasman
	 * @return arraylist of all issues in the domain.
	 */
	public ArrayList<Issue> getIssues()
	{
		Enumeration<Objective> issues=fObjectivesRoot.getPreorderIssueEnumeration();
		ArrayList<Issue> issuelist=new ArrayList<Issue>();
		while (issues.hasMoreElements()) issuelist.add((Issue)issues.nextElement());
		return issuelist;
	}

	/**
	 * @author erelsgl 
	 * @param issueName
	 * @return
	 */
	public IssueDiscrete discreteIssueByName(String issueName) {
		if (issueName==null) 
			return null;
		Issue issue = issuesByName.get(issueName);
		if (issue==null)
			throw new IllegalStateException("Unknown issue '"+issueName+"'");
		if (!(issue instanceof IssueDiscrete))
			throw new IllegalStateException("Unsupported issue type '"+issueName+"'");
		return (IssueDiscrete)issue;
	}
	
	/**
	 * @param issueName
	 * @return the issue with that name, or null if none is found. 
	 * @author erelsgl
	 */
	public Issue issueByName(String issueName) {
		return issuesByName.get(issueName);
	}

	
	/**
	 * Get the name of the discrete issue that contains this value.
	 * @param value must be lower-case.
	 * @return name of issue, or null if not found
	 */
	public String issueByValue(String value) {
		return issuesByValue.get(value);
	}

	public String valuesByIssue(String issueName) {
		Issue issue = issuesByName.get(issueName);
		if (issue==null)
			return "Unknown!";
		else
			return ((IssueDiscrete)issue).getValues().toString();
	}

	/**
	 * get number of all possible bids. Does not care of constraints. 
	 * 
	 * Not finished!!!
	 * 
	 * @return long number of all possible bids in the domain. 
	 */
	public long getNumberOfPossibleBids() {
		long lNumberOfPossibleBids = (long)1;
		ArrayList<Issue> lIssues = getIssues();
		for(Issue lIssue : lIssues) {
			switch(lIssue.getType()) {
			case DISCRETE:
				lNumberOfPossibleBids = lNumberOfPossibleBids * ((IssueDiscrete)lIssue).getNumberOfValues();
				break;
			case REAL:
				lNumberOfPossibleBids = lNumberOfPossibleBids * ((IssueReal)lIssue).getNumberOfDiscretizationSteps();
				break;
				//TODO: Finish getNumberOfPossibleBids() for Integer, Real and Price issues
/*			case INTEGER:
				lNumberOfPossibleBids = lNumberOfPossibleBids * ((IssueInteger)lIssue).get;
				break;
			}*/
			}//switch
		}
		return lNumberOfPossibleBids;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fObjectivesRoot == null) ? 0 : fObjectivesRoot.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Domain other = (Domain) obj;
		if (fObjectivesRoot == null) {
			if (other.fObjectivesRoot != null)
				return false;
		} else if (!fObjectivesRoot.equals(other.fObjectivesRoot))
			return false;
		return true;
	}

	public String getName() {
		return domainName;
	}

	public String getPathToDataFiles() {
		return pathToDataFiles;
	}
	
	
	public String getUtilitySpaceFilename(String owner, String type) {
		if (mapSideNameToUtilitySpace.get(owner)==null)
			throw new RuntimeException("No utility spaces for owner "+owner);
		String baseFilename = mapSideNameToUtilitySpace.get(owner).get(type);
		if (baseFilename==null)
			throw new RuntimeException("No utility spaces for owner "+owner+", type "+type);
		return pathToDataFiles+File.separator+baseFilename;
	}

	public UtilitySpace getUtilitySpace (String owner, String type) throws IOException {
		return new UtilitySpace(this, getUtilitySpaceFilename(owner, type));
	}
	
	/**
	 *  @throws IOException 
	 * @throws NegotiatorException 
	 * @see  negotiator.protocol.Protocol#loadWorldInformation
	 */
	public WorldInformation getAllUtilitySpaces(String owner) throws NegotiatorException, IOException {
		if (mapSideNameToUtilitySpace.get(owner)==null)
			throw new RuntimeException("No utility spaces for owner "+owner);
		WorldInformation worldInformation = new WorldInformation(new AgentID(owner), /*mediatorid=*/null);
		for (Entry<String, String> e: mapSideNameToUtilitySpace.get(owner).entrySet()) {
			worldInformation.addUtilitySpace(new UtilitySpace(this, pathToDataFiles+File.separator+e.getValue()));
		}
		return worldInformation;
	}
	
	public InputStream getPGFInputStream() throws NoNLPDataException {
		String baseFilename = "nlp_abs.pgf";
		try {
			return new FileInputStream(pathToDataFiles+File.separator+baseFilename);
		} catch (FileNotFoundException ex) {
			throw new NoNLPDataException("To use GF data for a domain, its data folder must contain the file '"+baseFilename+"', compiled by GF from the grammar files.");
		}
	}
	
	public String getConcreteGrammarName(String owner) throws NoNLPDataException { 
		String baseFilename = mapSideNameToNLPData.get(owner);
		if (baseFilename==null)
			throw new NoNLPDataException("No NLP data for owner "+owner+" in domain "+this.domainName);
		return baseFilename.replaceAll("[.][^.]*$", "");
	}

	/**
	 * Check if the bid contains values for each Issue in the domain
	 * @param bid - the bid to check
	 * @return true iff the Bid contain value for each issue in the domain
	 * @see #missingIssuesInBid
	 */
	public boolean isFullBid(Bid bid) {
		boolean res=true;
		for(Issue lIssue : getIssues()) {
			try {
				Value v=bid.getValue(lIssue.getNumber());
				// hack the value No Agreement in discrete value is consider not complete
				if (v==null || (v.getType() == ISSUETYPE.DISCRETE && ((ValueDiscrete)v).getValue().equalsIgnoreCase(ValueDiscrete.StrNoAgreement))) {
					res=false;
					break;
				}
			} catch (Exception e) {
				res=false;
			}
		}
		return res;
	}

	/**
	 * Check if the bid contains values for each Issue in the domain.
	 * @param bid - the bid to check
	 * @return a collection of missing issues in the bid.
	 * @see #isFullBid
	 */
	public Collection<Issue> missingIssuesInBid(Bid bid) {
		ArrayList<Issue> missingIssues = new ArrayList<Issue>();
		for(Issue lIssue : getIssues()) {
			try {
				Value v=bid.getValue(lIssue.getNumber());
				// hack the value No Agreement in discrete value is consider not complete
				if (v==null || (v.getType() == ISSUETYPE.DISCRETE && ((ValueDiscrete)v).getValue().equalsIgnoreCase(ValueDiscrete.StrNoAgreement))) {
					missingIssues.add(lIssue);
					break;
				}
			} catch (Exception e) {
				return missingIssues;
			}
		}
		return missingIssues;
	}

	/*
	 * TEST ZONE
	 */
	
	/**
	 * Create a file with random combinations of issue values, for Amazon Turk experiments.
	 * @param count
	 * @throws IOException 
	 */
	public void createRandomCombinationsFile(int count, Writer writer, String columnDelimiter) throws IOException {
		List<Issue> issues = this.getIssues();
		
		// Column headings:
		boolean first=true;
		for (Issue issue: issues) {
			if (!first) writer.append(columnDelimiter);
			writer.append(issue.getName().replaceAll("[^a-zA-Z]", ""));
			first=false;
		}
		writer.append("\n");

		// Column values:
		for (int i=0;i<count;i++) {
			first=true;
			for (Issue issue: issues) {
				String value = issue.getRandomValue().toString();
				if (!first) writer.append(columnDelimiter);
				writer.append('"')
				      .append(value)
				      .append('"');
				first=false;
			}
			writer.append("\n");
		}
		writer.close();
	}
	
	/**
	 * Demo program for testing only
	 * @author Erel Segal
	 * @date 2011-02-09
	 * 
	 * @param args[0] a path to an xml file holding domain info
	 * @throws Exception 
	 */
	public static void main(String args[]) throws Exception {
		Domain d = new Domain(args[0]);
		System.out.println("\n\nDomain name="+d.getName()+". All issues in the domain: \n"+d.getIssues().toString());
		System.out.println("\n\nAll utility spaces in the domain: \n"+d.mapSideNameToUtilitySpace.toString());
		System.out.println("utility space file name for employer-compromise: "+d.getUtilitySpaceFilename("employer","compromise"));
		System.out.println("side-name of 'Side_ACompromise.xml': "+d.getSideName("Side_ACompromise.xml"));
		System.out.println("personality of 'Side_ACompromise.xml': "+d.getPersonality("Side_ACompromise.xml"));
		System.out.println("concrete grammar of 'employer': "+d.getConcreteGrammarName("employer"));

		Issue firstIssue = d.getIssues().get(0);
		if (firstIssue instanceof IssueDiscrete) {
			System.out.println("\n\nAll values for first issue ("+firstIssue+"): \n"+((IssueDiscrete)firstIssue).getValues().toString());
		}
		//System.out.println("\n\nThe first objective in the domain: \n"+o.toXML());
		
		int seed = 1;
		IssueDiscrete.random.setSeed(seed);
		String columnDelimiter = "\t"; // for AWS CLT
		//d.createRandomCombinationsFile(12, new BufferedWriter(new FileWriter("mturk/bid.input")), columnDelimiter);
		IssueDiscrete.random.setSeed(seed);
		d.createRandomCombinationsFile(12, new BufferedWriter(new FileWriter("mturk/values.input")), columnDelimiter);
	}
}
