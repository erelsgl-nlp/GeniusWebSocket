package negotiator.repository;

import java.util.ArrayList;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class RepositoryItemTypeAdapter extends XmlAdapter<RepositoryItemType, ArrayList<RepItem>> {

    // adapt original Java construct to a type, NotificationsType,
    // which we can easily map to the XML output we want
    public RepositoryItemType marshal(ArrayList<RepItem> events) throws Exception {
        ArrayList<DomainRepItem> domains = new ArrayList<DomainRepItem>();
        ArrayList<AgentRepItem> agents = new ArrayList<AgentRepItem>();
        ArrayList<ProtocolRepItem> protocols = new ArrayList<ProtocolRepItem>();
        ArrayList<MediatorRepItem> mediators = new ArrayList<MediatorRepItem>();
        for (RepItem e : events) {
            if (e instanceof DomainRepItem) {
                domains.add((DomainRepItem)e);
            } else if (e instanceof AgentRepItem) {
                agents.add((AgentRepItem)e);              
            } else if (e instanceof ProtocolRepItem) {
            	protocols.add((ProtocolRepItem)e);
            } else if (e instanceof MediatorRepItem) {
            	mediators.add((MediatorRepItem)e);
            }else throw new Exception("Repository: unknow item");
        }        
        return new RepositoryItemType(agents, domains, protocols,mediators);
    }

    // map XML type to Java
    public ArrayList<RepItem> unmarshal(RepositoryItemType notifications) throws Exception {
        ArrayList<RepItem> events = new ArrayList<RepItem>();
        events.addAll(notifications.getAgentRepItem());
        events.addAll(notifications.getDomainRepItem());
        events.addAll(notifications.getProtocolRepItem());
        events.addAll(notifications.getMediatorRepItem());
        return events;
    }
}
  
