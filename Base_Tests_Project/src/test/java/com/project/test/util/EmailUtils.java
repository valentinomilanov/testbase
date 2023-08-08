package com.project.test.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.FolderClosedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.SearchTerm;

import org.apache.commons.lang3.StringUtils;

import com.project.test.constants.EmailAccount;

public class EmailUtils {

	private Folder folder;

	public enum EmailFolder {
		INBOX("INBOX"),
		SPAM("SPAM"), 
		SENT("SENT");

		private String text;

		private EmailFolder(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}
	}

	private String username;
	private String password;
	private String server;
	private EmailFolder emailFolder;
	
	/**
	 * Uses email.username and email.password properties from the properties file. Reads from Inbox folder of the email application.
	 * @throws MessagingException
	 */
	public EmailUtils() throws MessagingException {
		this(EmailFolder.INBOX);
	}

	/**
	 * Uses username and password in properties file to read from a given folder of the email application.
	 * @param emailFolder Folder in email application to interact with
	 * @throws MessagingException
	 */
	public EmailUtils(EmailFolder emailFolder) throws MessagingException {
		this(getEmailUsernameFromProperties(),
				getEmailPasswordFromProperties(),
				getEmailServerFromProperties(),
				emailFolder);
	}
	
	public EmailUtils(EmailAccount account) throws MessagingException {
	    this(account.getEmail(), account.getPassword(), "mail.wedoqa.co", EmailFolder.INBOX);
    }
	
	/**
	 * Connects to email server with credentials provided to read from a given folder of the email application.
	 * @param username Email username (e.g. janedoe@email.com)
	 * @param password Email password
	 * @param server Email server (e.g. smtp.email.com)
	 * @param emailFolder Folder in email application to interact with
	 */
	public EmailUtils(String username, String password, String server, EmailFolder emailFolder) throws MessagingException {
		initializeConnection(username, password, server, emailFolder);
		this.username = username;
		this.password = password;
		this.server = server;
		this.emailFolder = emailFolder;
	}
	
	private void initializeConnection(String username, String password, String server, EmailFolder emailFolder) throws MessagingException {
	    Properties props = System.getProperties();
        try {
            props.load(new FileInputStream(new File("src/test/resources/email.properties")));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        Session session = Session.getInstance(props);
        Store store = session.getStore("imaps");
        store.connect(server, username, password);


        folder = store.getFolder(emailFolder.getText());
        folder.open(Folder.READ_WRITE);
	}
	
	//************* GET EMAIL PROPERTIES *******************

		public static String getEmailAddressFromProperties() {
			return System.getProperty("email.address");
		}

		public static String getEmailUsernameFromProperties() {
			return System.getProperty("email.username");
		}

		public static String getEmailPasswordFromProperties() {
			return System.getProperty("email.password");
		}

		public static String getEmailProtocolFromProperties() {
			return System.getProperty("email.protocol");
		}

		public static int getEmailPortFromProperties() {
			return Integer.parseInt(System.getProperty("email.port"));
		}

		public static String getEmailServerFromProperties() {
			return System.getProperty("email.server");
		}
		
		//************* EMAIL ACTIONS *******************

		public void openEmail(Message message) throws Exception{
			message.getContent();
		}

		public int getNumberOfMessages() throws MessagingException {
			return folder.getMessageCount();
		}

		public int getNumberOfUnreadMessages()throws MessagingException {
			return folder.getUnreadMessageCount();
		}

		/**
		 * Gets a message by its position in the folder. The earliest message is indexed at 1.
		 */
		public Message getMessageByIndex(int index) throws MessagingException {
			return folder.getMessage(index);
		}

		public Message getLatestMessage() throws MessagingException {
			return getMessageByIndex(getNumberOfMessages());
		}

		/**
		 * Gets all messages within the folder.
		 */
		public Message[] getAllMessages() throws MessagingException {
			return folder.getMessages();
		}

		/**
		 * @param maxToGet maximum number of messages to get, starting from the latest. For example, enter 100 to get the last 100 messages received.
		 */
		public Message[] getMessages(int maxToGet) throws MessagingException {
			Map<String, Integer> indices = getStartAndEndIndices(maxToGet);
			return folder.getMessages(indices.get("startIndex"), indices.get("endIndex"));
		}

		/**
		 * Searches for messages with a specific subject.
		 * @param subject Subject to search messages for
		 * @param unreadOnly Indicate whether to only return matched messages that are unread
		 * @param maxToSearch maximum number of messages to search, starting from the latest. For example, enter 100 to search through the last 100 messages.
		 *//*
		public Message[] getMessagesBySubject(String subject, boolean unreadOnly, int maxToSearch) throws Exception{
			Map<String, Integer> indices = getStartAndEndIndices(maxToSearch);

			Message[] messages = folder.search(
					new SubjectTerm(subject),
					folder.getMessages(indices.get("startIndex"), indices.get("endIndex")));

			if (unreadOnly) {
				List<Message> unreadMessages = new ArrayList<Message>();
				for (Message message : messages) {
					if (isMessageUnread(message)) {
						unreadMessages.add(message);
					}
				}
				messages = unreadMessages.toArray(new Message[]{});
			}

			return messages;
		}*/

		public Message waitMessageBySubject(String subject, String email) throws Exception {
			return waitMessageBySubject(subject, email, true);
		}

		public Message waitMessageBySubject(String subject, String email, boolean isUnreadOnly) throws Exception {
		    return waitMessageBySubject(Arrays.asList(subject), email, isUnreadOnly, 360);
		}
		
		public Message waitMessageBySubject(List<String> subjects, String email) throws Exception {
	        return waitMessageBySubject(subjects, email, true);
	    }

	    public Message waitMessageBySubject(List<String> subjects, String email, boolean isUnreadOnly) throws Exception {
	        return waitMessageBySubject(subjects, email, isUnreadOnly, 360);
	    }
		
	    public Message waitMessageBySubject(String subjects, String email, boolean isUnreadOnly, int maxWait) throws Exception {
	        return waitMessageBySubject(Arrays.asList(subjects), email, isUnreadOnly, maxWait);
	    }
	    
		public Message waitMessageBySubject(List<String> subjects, String email, boolean isUnreadOnly, int maxWait) throws Exception {
	        int count = 0;
	        List<Message> messages = Arrays.asList(getMessages(subjects, email, isUnreadOnly, 100));
	        while (messages.isEmpty() && count < maxWait) {
	            Thread.sleep(1500);
	            count++;
	            messages = Arrays.asList(getMessages(subjects, email, isUnreadOnly, 100));
	        }
	        if (messages.isEmpty()) {
	            throw new Exception("The email did not arrived: " + subjects);
	        } else {
	            return messages.get(0);
	        }
	    }
		
		public Message[] getMessages(String subject, String email, boolean unreadOnly, int maxToSearch) throws Exception {
		    return getMessages(Arrays.asList(subject), email, unreadOnly, maxToSearch);
		}
		
		public Message[] getMessages(List<String> subjects, String email, boolean unreadOnly, int maxToSearch) throws Exception {
			try {
			      Map<String, Integer> indices = getStartAndEndIndices(maxToSearch);

			        //create new searchTerm
			        SearchTerm term = new SearchTerm() {
			            private static final long serialVersionUID = 1L;

			            public boolean match(Message message) {
			                try {
			                    for (String subject: subjects) {
			                        if (message.getSubject().contains(subject) && 
			                                (
			                                        email.isEmpty() 
			                                        || (message.getAllRecipients() != null && message.getAllRecipients()[0].toString().contains(email)) 
			                                        
			                                        )
			                                ) {
			                            return true;
			                        }
			                    }
			                } catch (MessagingException ex) {
			                    ex.printStackTrace();
			                }
			                return false;
			            }
			        };
			    
	    		//search email with created searchTerm
	    		Message[] messages = folder.search(term, folder.getMessages(indices.get("startIndex"), indices.get("endIndex")));
	    	    
	    		//return only unreded emails if need
	            if (unreadOnly) {
	                List<Message> unreadMessages = new ArrayList<Message>();
	                for (Message message : messages) {
	                    if (isMessageUnread(message)) {
	                        unreadMessages.add(message);
	                    }
	                }
	                messages = unreadMessages.toArray(new Message[]{});
	            }
	            return messages;
			} catch (FolderClosedException e) {
			    initializeConnection(username, password, server, emailFolder);
			    return getMessages(subjects, email, unreadOnly, maxToSearch);
			}

		}

		public Message waitMessage(String subject, String email) throws Exception {
		    return waitMessage(subject, email, true, 50);
	    }

		public Message waitMessage(String subject, String email, boolean unreadOnly, int maxToSearch) throws Exception {
			int maxWait = 360;
			int count = 0;
			List<Message> messages = Arrays.asList(getMessages(Arrays.asList(subject), email, unreadOnly, maxToSearch));

			while (messages.isEmpty() && count < maxWait) {
				Thread.sleep(1000);
				count++;
				messages = Arrays.asList(getMessages(Arrays.asList(subject), email, unreadOnly, maxToSearch));
			}
			if (messages.isEmpty()) {
				throw new Exception("The email did not arrived");
			} else {
				return messages.get(0);
			}
		}

		public void readMessages(String subject, String email, int minCount) throws Exception {
			int maxWait = 5;
			int count = 0;
			List<Message> messages = Arrays.asList(getMessages(Arrays.asList(subject), email, true, 100));
			while (messages.size() < minCount && count < maxWait) {
				Thread.sleep(1000);
				count++;
				messages = Arrays.asList(getMessages(Arrays.asList(subject), email, true, 100));
			}
			messages.stream().forEach(message -> {
			    try{
			        message.setFlag(Flags.Flag.SEEN, true);
			    } catch (MessagingException ex) {
			        ex.printStackTrace();
			    }
			});
		}

		public String getValueFromMail(String prefix, Message message) throws Exception {
			String emailContent = getMessageContent(message);
			String result = emailContent.substring(emailContent.indexOf(prefix));
			result = result.substring(0, result.indexOf("</div>"));
			result = result.substring(result.lastIndexOf(">") + 1);
			result.replaceAll("3D", "");
			return result; 
		}

		public String getValueFromMailMultiAuth(String prefix, Message message) throws Exception {
			String emailContent = getMessageContent(message);
			String result = emailContent.substring(emailContent.indexOf(prefix));
			result = result.substring(12, result.indexOf("</html>"));
			result = result.substring(result.lastIndexOf(">") + 1);
			result.replaceAll("3D", "");
			return result;
		}
		
		public String getValueFromHelpdeskMail(String prefix, Message message) throws Exception {
			String emailContent = getMessageContent(message);
			String result = emailContent.substring(emailContent.indexOf(prefix));
			result = result.substring(result.indexOf("</div>") + 6, result.indexOf("<br/>"));
			result = result.replace("&nbsp;", "").trim();
			return result; 
		}
		
		public String getDownloadStudyLinkFromMail(String prefix, Message message) throws Exception {
			String emailContent = getMessageContent(message);
			String result = emailContent.substring(emailContent.indexOf(prefix),emailContent.length());
			result = result.substring(0, result.indexOf("'>"));
			return result; 
		}

		/* 
		 * Old getValueFromMail method, could be useful at some point 
		 * 
		public String getValueFromMail(String prefix, Message message) throws Exception {
			BufferedReader reader = new BufferedReader(new InputStreamReader(message.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.replaceAll("amp;", "").replaceAll("&nbsp;", "").trim();
				if (line.startsWith("<html>")) {
					List<String> rowsWithHtml = Arrays.asList(line.split("(<br>|<br\\/>)"));
					List<String> rowsWithoutHtml = rowsWithHtml.stream()
							.map(row -> row.replaceAll("<[^>]*>", ""))
							.collect(Collectors.toList());
					Optional<String> match = rowsWithoutHtml.stream()
						.filter(row -> row.startsWith(prefix.replace("//", "/"))).findFirst();
					if (match.isPresent()) {
						return match.get().replace(prefix, "").trim();
					}
				} else {
					String textOnly = line.replaceAll("<[^>]*>", "");
					if(textOnly.startsWith(prefix)) {
						return textOnly.replace(prefix, "").trim();
					}
				}
			}
			throw new Exception("The prefix not found in email\nPrefix: " + prefix + "\nContent:\n" + message.getContent());
		} */

		public String getEmail(Message message) throws MessagingException, IOException {
			String all = getTextFromMessage(message);
			Integer start = all.lastIndexOf("Name:");
			Integer startSubString = start+5;
			Integer end = all.indexOf("Password:");
			String email = all.substring(startSubString,end).trim();
			if(email.endsWith("-"))
				email = email.replace("-", "").trim();
			return email;
		}

		public String getUsername(Message message) throws MessagingException, IOException {
			String all = getTextFromMessage(message);
			Integer start = all.lastIndexOf("Username:");
			Integer startSubString = start+9;
			Integer end = all.indexOf("Password:");;
			String username = all.substring(startSubString,end).trim();
			return username;
		}

		public String getPassword(Message message) throws MessagingException, IOException {
			String all = getTextFromMessage(message);
			Integer start = all.lastIndexOf("Password:");
			Integer startSubString = start+9;
			Integer endSubString = startSubString+18;
			String pass = all.substring(startSubString,endSubString).trim();
			return pass;
		}

		public String getTextFromMessage(Message message) throws MessagingException, IOException {
			String result = "";
			if (message.isMimeType("text/plain")) {
				result = message.getContent().toString();
			} else if (message.isMimeType("multipart/*")) {
				MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
				result = getTextFromMimeMultipart(mimeMultipart);
			}
			return result;
		}

		private String getTextFromMimeMultipart(
				MimeMultipart mimeMultipart)  throws MessagingException, IOException{
			String result = "";
			int count = mimeMultipart.getCount();
			for (int i = 0; i < count; i++) {
				BodyPart bodyPart = mimeMultipart.getBodyPart(i);
				if (bodyPart.isMimeType("text/plain")) {
					result = result + "\n" + bodyPart.getContent();
					break;
				} else if (bodyPart.isMimeType("text/html")) {
					String html = (String) bodyPart.getContent();
					result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
				} else if (bodyPart.getContent() instanceof MimeMultipart){
					result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
				}
			}
			return result;
		}

		/**
		 * Returns HTML of the email's content.
		 */
		public String getMessageContent(Message message) throws Exception {
			StringBuffer buffer = new StringBuffer();
			BufferedReader reader = new BufferedReader(new InputStreamReader(message.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
			    if (line.endsWith("=")) {
			        line = line.substring(0, line.length() - 1);
			    }
				buffer.append(line);
			}
			return buffer.toString();
		}

		/**
		 * Returns all urls from an email message with the linkText specified.
		 */
		public List<String> getUrlsFromMessage(Message message, String linkText) throws Exception {
			String html = getMessageContent(message);
			List<String> allMatches = new ArrayList<String>();
			Matcher matcher = Pattern.compile("(<a [^>]+>)" + linkText + "</a>").matcher(html);
			while (matcher.find()) {
				String aTag = matcher.group(1);
				aTag = aTag.replace("3D", "");
				allMatches.add(aTag.substring(aTag.indexOf("http"), aTag.indexOf(">") - 1));
			}
			return allMatches;
		}

		/**
		 *
		 * Returns an url from an email message with the linkText specified.<br>
		 * <b> WARNING </b><br>
		 * The method removes "amp;" part of the url and splits it from the ' quote character.
		 */
		public String getUrlFromMessage(Message message, String linkText) throws Exception {
			String html = getMessageContent(message);
			String allMatches = new String();
			Matcher matcher = Pattern.compile("(<a [^>]+>)" + linkText + "</a>").matcher(html);
			if (matcher.find()) {
				String aTag = matcher.group(1);
				allMatches = aTag.substring(aTag.indexOf("http"), aTag.indexOf(">") - 1);
			}
			allMatches = allMatches.replace("amp;", "").replace("3D", "");
			int index = allMatches.indexOf("'");
			return allMatches.substring(0, index);
		}

		public String getUrlFromMessageHttps(Message message, String linkText) throws Exception {
			String html = getMessageContent(message);
			String allMatches = new String();
			Matcher matcher = Pattern.compile("(<a [^>]+>)" + linkText + "</a>").matcher(html);
			if (matcher.find()) {
				String aTag = matcher.group(1);
				allMatches = aTag.substring(aTag.indexOf("https"), aTag.indexOf(">") - 1);
			}
			allMatches = allMatches.replace("amp;", "").replace("3D", "");
			int index = allMatches.indexOf("\"");
			return allMatches.substring(0, index);
		}

		public String getUrlFromMessageForAccLocked(Message message, String linkText) throws Exception {
			String html = getMessageContent(message);
			String allMatches = new String();
			Matcher matcher = Pattern.compile("(<a [^>]+>)" + linkText + "</a>").matcher(html);
			if (matcher.find()) {
				String aTag = matcher.group(1);
				allMatches = aTag.substring(aTag.indexOf("http"), aTag.indexOf(">") - 1);
			}
			allMatches = allMatches.replace("amp;", "").replace("3D", "");
			int index = allMatches.indexOf("\"");
			return allMatches.substring(0, index);
		}

		public String getUrlFromMessageForUnsub(Message message, String linkText) throws Exception {
			String html = getMessageContent(message);
			String allMatches = new String();
			Matcher matcher = Pattern.compile("(<a [^>]+>)" + linkText + "</a>").matcher(html);
			if (matcher.find()) {
				String aTag = matcher.group(1);
				allMatches = aTag.substring(aTag.indexOf("http"), aTag.indexOf(">") - 1);
			}
			allMatches = allMatches.replace("amp;", "").replace("3D", "");
			return allMatches;
		}

		private Map<String, Integer> getStartAndEndIndices(int max) throws MessagingException {
			int endIndex = getNumberOfMessages();
			int startIndex = endIndex - max;

			//In event that maxToGet is greater than number of messages that exist
			if (startIndex < 1) {
				startIndex = 1;
			}

			Map<String, Integer> indices = new HashMap<String, Integer>();
			indices.put("startIndex", startIndex);
			indices.put("endIndex", endIndex);

			return indices;
		}

		/**
		 * Searches an email message for a specific string.
		 */
		public boolean isTextInMessage(Message message, String text) throws Exception {
			String content = getMessageContent(message);

			//Some Strings within the email have whitespace and some have break coding. Need to be the same.
			content = content.replace("&nbsp;", " ");
			return content.contains(text);
		}

		public boolean isMessageInFolder(String subject, String email, boolean unreadOnly) throws Exception {
			int messagesFound = getMessages(Arrays.asList(subject), email, unreadOnly, Math.min(getNumberOfMessages(), 200)).length;
			return messagesFound > 0;
		}

		public boolean isMessageUnread(Message message) throws Exception {
			return !message.isSet(Flags.Flag.SEEN);
		}
		
		public List<InputStream> getAttachments(Message message) throws Exception {
		    Object content = message.getContent();
		    if (content instanceof String)
		        return null;        

		    if (content instanceof Multipart) {
		        Multipart multipart = (Multipart) content;
		        List<InputStream> result = new ArrayList<InputStream>();

		        for (int i = 0; i < multipart.getCount(); i++) {
		            result.addAll(getAttachments(multipart.getBodyPart(i)));
		        }
		        return result;
		    }
		    return null;
		}

		private List<InputStream> getAttachments(BodyPart part) throws Exception {
		    List<InputStream> results = new ArrayList<InputStream>();
		    Object content = part.getContent();
		    if (content instanceof InputStream || content instanceof String) {
		        if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()) || StringUtils.isNotBlank(part.getFileName())) {
		            results.add(part.getInputStream());
		            return results;
		        } else {
		            return new ArrayList<InputStream>();
		        }
		    }

		    if (content instanceof Multipart) {
		            Multipart multipart = (Multipart) content;
		            for (int i = 0; i < multipart.getCount(); i++) {
		                BodyPart bodyPart = multipart.getBodyPart(i);
		                results.addAll(getAttachments(bodyPart));
		            }
		    }
		    return results;
		}
}
