/*
 * Created on Jan 17, 2008 by Zach Bailey
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
import javax.mail.internet.MimeMessage;

/**
 * This plug-in does some really neat stuff!
 * @author <Your Name Here>
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
			//only on publish to production
			if(page.getLastPublishedOn() == null)
			{
				if(information.isUnpublish() == false)
				{
					if(page.getDataDefinitionPath().equalsIgnoreCase("Event"))
					{
						//if(information.getDestinationName().equalsIgnoreCase("Production bethel.edu"))//this is for cms.bethel.edu
						if(information.getDestinationName().equalsIgnoreCase("public www html"))//this is for testing in web.xp
						{
							System.out.println("DestinationName: " + information.getDestinationName());
							System.out.println("Event page, first timed published. Next step: email. Path: " + information.getEntityPath());
							
							////////////////////////////////
							////////////////////////////////
							////////////////////////////////
							/*Properties props = new Properties();
							Session session = Session.getDefaultInstance(props, null);
	
						    try {
						        MimeMessage msg = new MimeMessage(session);
						        msg.setFrom("no-reply@bethel.edu");
						        msg.setRecipients(Message.RecipientType.TO,
						                          "mw-engstrom@bethel.edu");
						        msg.setSubject("JavaMail hello world example");
						        msg.setSentDate(new Date());
						        msg.setText("Hello, world!\n");
						        Transport.send(msg);
						    } catch (MessagingException mex) {
						        System.out.println("send failed, exception: " + mex);
						    }*/
							////////////////////////////////
							////////////////////////////////
							////////////////////////////////
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