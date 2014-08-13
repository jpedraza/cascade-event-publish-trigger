/*
 *  Based off an example created on Jan 17, 2008 by Zach Bailey
 *  This version created for Bethel University on Jul 25, 2014
 *
 * This software is offered as-is with no license and is free to reproduce or use as anyone sees fit.
 */
package edu.bethel.cascade.plugin;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.cms.publish.PublishTrigger;
import com.cms.publish.PublishTriggerEntityTypes;
import com.cms.publish.PublishTriggerException;
import com.cms.publish.PublishTriggerInformation;
import com.hannonhill.cascade.api.asset.common.BaseAsset;
import com.hannonhill.cascade.api.asset.common.Identifier;
import com.hannonhill.cascade.api.asset.home.Page;
import com.hannonhill.cascade.api.operation.Read;
import com.hannonhill.cascade.api.operation.result.ReadOperationResult;
import com.hannonhill.cascade.model.dom.identifier.EntityType;
import com.hannonhill.cascade.model.dom.identifier.EntityTypes;

import javax.mail.*;
import javax.mail.internet.*;
/**
 * Publish trigger to email authors of events when they are published for the first time
 */
public class EventAuthorEmailPublishTrigger implements PublishTrigger
{
    private Map<String, String> parameters = new HashMap<String, String>();
    private PublishTriggerInformation information;
    
    /* (non-Javadoc)
     * @see com.cms.publish.PublishTrigger#invoke()
     */
    public void invoke() throws PublishTriggerException
    {
        // this is where the logic for the trigger lives.
        // we switch on the entity type and this allows us to determine if a page or file is being published
        try 
        {
			Page page = (Page) readAsset(information.getEntityId(), EntityTypes.TYPE_PAGE);
			if(page.getDataDefinitionPath().equalsIgnoreCase("Event")) //Makes sure the page is an event
			{
				if(page.getCreatedBy().equalsIgnoreCase("tinker")) //Only email author for events created by tinker
				{
					if(information.isUnpublish() == false) //Checks to make sure we are publishing the event
					{
						if(page.getLastPublishedOn() == null) //Makes sure this is the first time this event is being published
						{
							//if(information.getDestinationName().equalsIgnoreCase("public www html"))//this is for testing in web.xp
							if(information.getDestinationName().equalsIgnoreCase("Production bethel.edu"))//this is for cms.bethel.edu
							{
								String to = page.getMetadata().getAuthor() + "@bethel.edu";
								//String to = "mw-engstrom@bethel.edu";
								String from = "no-reply@bethel.edu";
								String host = "localhost";
								Properties properties = System.getProperties();
								properties.setProperty("mail.smtp.host", host);
								Session session = Session.getDefaultInstance(properties);
								try{
									MimeMessage message = new MimeMessage(session);
									message.setFrom(new InternetAddress(from));
									message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
									message.setSubject("Your event has been published");
									message.setContent("Your event, [" + page.getMetadata().getTitle() + "], has been approved. Visit <a href=\"https://tinker.bethel.edu/event/\">tinker.bethel.edu</a> to see your new event page or make changes to it.<br/><br/>If you have any questions, please contact Conference and Event Services.<br/><br/>Conference and Event Services<br/><a href=\"mailto:event-services@bethel.edu\">event-services@bethel.edu</a><br/>651.638.6090", "text/html; charset=utf-8");
									Transport.send(message);
									System.out.println("Sent message successfully to " + to);
								}catch(MessagingException mex){
									mex.printStackTrace();
								}
							}
						}
					}
				}
			}
		}
        catch (Exception e) 
        {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

    private BaseAsset readAsset(String id, EntityType type) throws Exception
    {
        Read read = new Read();
        Identifier toRead = new IdentifierImpl(id, type);
        read.setToRead(toRead);
        read.setUsername("system");
        ReadOperationResult result = (ReadOperationResult) read.perform();
        return result.getAsset();
    }

    private class IdentifierImpl implements Identifier
    {
        private final String id;
        private final EntityType type;

        public IdentifierImpl(String id, EntityType type)
        {
            this.id = id;
            this.type = type;
        }

        public String getId()
        {
            return id;
        }

        public EntityType getType()
        {
            return type;
        }
    }
    
    /* (non-Javadoc)
     * @see com.cms.publish.PublishTrigger#setParameter(java.lang.String, java.lang.String)
     */
    public void setParameter(String name, String value)
    {
        // let's just store our parameters in a Map for access later
        parameters.put(name, value);
    }

    /* (non-Javadoc)
     * @see com.cms.publish.PublishTrigger#setPublishInformation(com.cms.publish.PublishTriggerInformation)
     */
    public void setPublishInformation(PublishTriggerInformation information)
    {
        // store this in an instance member so invoke() has access to it
        this.information = information;
    }
}