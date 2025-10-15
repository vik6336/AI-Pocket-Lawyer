-- AI Pocket Lawyer Database Schema
-- MySQL Database Setup

CREATE DATABASE IF NOT EXISTS pocket_lawyer;
USE pocket_lawyer;

-- Table: legal_categories
CREATE TABLE IF NOT EXISTS legal_categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: legal_qa (Question-Answer pairs for chatbot)
CREATE TABLE IF NOT EXISTS legal_qa (
    qa_id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT,
    question TEXT NOT NULL,
    answer TEXT NOT NULL,
    keywords VARCHAR(500),
    priority INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES legal_categories(category_id) ON DELETE SET NULL,
    INDEX idx_keywords (keywords(255))
);

-- Table: legal_rights (Rights information hub)
CREATE TABLE IF NOT EXISTS legal_rights (
    right_id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    details LONGTEXT,
    source VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES legal_categories(category_id) ON DELETE SET NULL,
    INDEX idx_title (title)
);

-- Table: document_templates
CREATE TABLE IF NOT EXISTS document_templates (
    template_id INT AUTO_INCREMENT PRIMARY KEY,
    template_name VARCHAR(255) NOT NULL,
    template_type VARCHAR(100) NOT NULL,
    template_content LONGTEXT NOT NULL,
    fields_json TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table: chat_history (User conversation history)
CREATE TABLE IF NOT EXISTS chat_history (
    chat_id INT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(100),
    user_query TEXT NOT NULL,
    bot_response TEXT NOT NULL,
    category_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES legal_categories(category_id) ON DELETE SET NULL,
    INDEX idx_session (session_id)
);

-- Table: generated_documents (Track generated documents)
CREATE TABLE IF NOT EXISTS generated_documents (
    doc_id INT AUTO_INCREMENT PRIMARY KEY,
    template_id INT,
    file_name VARCHAR(255),
    file_path VARCHAR(500),
    format VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (template_id) REFERENCES document_templates(template_id) ON DELETE SET NULL
);

-- Insert default legal categories
INSERT INTO legal_categories (category_name, description) VALUES
('Consumer Rights', 'Rights related to consumer protection and product/service issues'),
('Tenant Rights', 'Rights related to rental agreements and landlord-tenant disputes'),
('Employee Rights', 'Rights related to employment, workplace, and labor laws'),
('Women Rights', 'Rights specific to women including safety, equality, and protection'),
('Cyber Rights', 'Rights related to online privacy, cybercrime, and digital security'),
('General Legal', 'General legal queries and miscellaneous topics');

-- Insert sample Q&A data
INSERT INTO legal_qa (category_id, question, answer, keywords, priority) VALUES
(1, 'What are my rights if I receive a defective product?', 
'Under the Consumer Protection Act, you have the right to: 1) Return the defective product within the warranty period, 2) Get a replacement or refund, 3) File a complaint with the consumer forum if the seller refuses. Keep your purchase receipt and warranty card as proof.', 
'defective,product,consumer,warranty,refund,replacement', 10),

(1, 'Can a shop refuse to give me a refund?', 
'A shop can refuse a refund if: 1) The product is not defective, 2) You changed your mind after purchase (unless they have a return policy), 3) You damaged the product yourself. However, for defective products, refunds are mandatory under consumer law.', 
'refund,shop,return,consumer,policy', 8),

(2, 'Can my landlord increase rent without notice?', 
'No. Your landlord must provide advance notice (typically 30-90 days depending on local laws) before increasing rent. The rent increase should also be reasonable and in accordance with your rental agreement terms.', 
'rent,increase,landlord,notice,tenant', 10),

(2, 'What can I do if my landlord refuses to return my security deposit?', 
'If your landlord refuses to return your security deposit: 1) Send a written notice requesting the deposit, 2) Check your rental agreement for deposit return terms, 3) File a complaint with the rent control board, 4) Consider small claims court if the amount is significant.', 
'security,deposit,landlord,return,tenant', 9),

(3, 'Can my employer fire me without notice?', 
'It depends on your employment contract and local labor laws. Generally: 1) Permanent employees require notice period or severance pay, 2) Probationary employees may have different terms, 3) Termination for misconduct may not require notice. Check your employment contract and local labor laws.', 
'fire,termination,notice,employer,employee', 10),

(3, 'Am I entitled to paid leave?', 
'Yes, most labor laws mandate: 1) Casual leave (typically 7-12 days/year), 2) Sick leave (typically 7-14 days/year), 3) Earned/privilege leave (typically 15-30 days/year). The exact number depends on your country/state labor laws and company policy.', 
'leave,paid,sick,casual,employee', 8),

(4, 'What should I do if I face workplace harassment?', 
'If you face workplace harassment: 1) Document all incidents with dates and details, 2) Report to your HR department or Internal Complaints Committee (ICC), 3) File a written complaint, 4) If unresolved, approach the police or labor commissioner, 5) Seek legal counsel if needed.', 
'harassment,workplace,women,complaint,ICC', 10),

(4, 'What are my rights in case of domestic violence?', 
'Under domestic violence laws, you have the right to: 1) File an FIR at the police station, 2) Seek a protection order from the court, 3) Request residence rights, 4) Claim maintenance and compensation, 5) Access shelter homes and legal aid. Contact women helpline (181 in India) for immediate assistance.', 
'domestic,violence,women,protection,FIR', 10),

(5, 'What should I do if someone hacks my social media account?', 
'If your account is hacked: 1) Immediately try to recover using forgot password, 2) Report to the platform (Facebook, Instagram, etc.), 3) Change passwords of linked accounts, 4) File a cybercrime complaint online or at the nearest cyber cell, 5) Inform your contacts about the hack.', 
'hack,social,media,account,cyber,password', 9),

(5, 'Is online defamation a crime?', 
'Yes, online defamation is a crime under cyber laws. If someone posts false and damaging content about you online: 1) Take screenshots as evidence, 2) Send a legal notice to remove the content, 3) File a complaint with the cyber cell, 4) You can also file a civil defamation suit for damages.', 
'defamation,online,cyber,social,media', 8),

(6, 'How do I file an FIR?', 
'To file an FIR (First Information Report): 1) Visit the nearest police station, 2) Provide details of the incident in writing, 3) The police must register your FIR (they cannot refuse), 4) Get a copy of the FIR with a unique number, 5) If police refuse, you can approach the Superintendent of Police or file online.', 
'FIR,police,complaint,crime,report', 10),

(6, 'Do I need a lawyer for small claims?', 
'For small claims (typically under $5000-10000 depending on jurisdiction): 1) Small claims court is designed for self-representation, 2) Procedures are simplified, 3) However, a lawyer can help prepare your case better, 4) Legal aid may be available for low-income individuals. Weigh the cost vs. benefit.', 
'lawyer,small,claims,court,legal', 7);

-- Insert sample legal rights data
INSERT INTO legal_rights (category_id, title, description, details, source) VALUES
(1, 'Right to Information', 
'Consumers have the right to know about the quality, quantity, potency, purity, standard, and price of goods.',
'The Consumer Protection Act ensures that consumers are protected from unfair trade practices. You have the right to:\n\n1. Complete product information\n2. Manufacturing and expiry dates\n3. Ingredients and composition\n4. Price details (MRP)\n5. Safety warnings and usage instructions\n\nIf a seller refuses to provide this information, you can file a complaint with the consumer forum.',
'Consumer Protection Act, 2019'),

(1, 'Right to Choose', 
'Consumers have the right to choose from a variety of goods at competitive prices.',
'You cannot be forced to buy a particular brand or product. This right includes:\n\n1. Freedom to select products\n2. Access to competitive markets\n3. Protection from monopolistic practices\n4. Right to reject poor quality goods\n\nSellers cannot force bundled purchases or deny alternatives.',
'Consumer Protection Act, 2019'),

(2, 'Right to Habitable Housing', 
'Tenants have the right to live in safe and habitable conditions.',
'Your landlord must provide:\n\n1. Basic amenities (water, electricity)\n2. Structural safety\n3. Pest-free environment\n4. Working plumbing and heating\n5. Safe locks and security\n\nIf these are not provided, you can:\n- Withhold rent (with legal notice)\n- Request repairs in writing\n- File complaint with housing authority\n- Terminate lease in extreme cases',
'Rent Control Acts (varies by state)'),

(2, 'Right to Privacy', 
'Tenants have the right to privacy and peaceful enjoyment of the property.',
'Your landlord cannot:\n\n1. Enter without notice (except emergencies)\n2. Harass or disturb you\n3. Change locks without permission\n4. Remove your belongings\n\nTypically, landlords must give 24-48 hours notice before entry. Document any violations and send written complaints.',
'Tenancy Laws'),

(3, 'Right to Fair Wages', 
'Employees have the right to receive fair wages for their work.',
'Under labor laws, you are entitled to:\n\n1. Minimum wage as per state/national standards\n2. Timely payment (typically monthly)\n3. Overtime pay for extra hours\n4. Equal pay for equal work\n5. Salary slips and documentation\n\nNon-payment or delayed payment can be reported to the labor commissioner.',
'Minimum Wages Act, Payment of Wages Act'),

(3, 'Right to Safe Working Conditions', 
'Employees have the right to work in a safe and healthy environment.',
'Your employer must provide:\n\n1. Safe equipment and machinery\n2. Protective gear where needed\n3. Clean and hygienic workspace\n4. First aid facilities\n5. Reasonable working hours\n6. Rest breaks\n\nReport unsafe conditions to your HR or labor department immediately.',
'Occupational Safety and Health Laws'),

(4, 'Right to Equal Treatment', 
'Women have the right to equal treatment in all spheres of life.',
'Constitutional and legal rights include:\n\n1. Equal pay for equal work\n2. No gender discrimination in employment\n3. Equal property rights\n4. Equal education opportunities\n5. Protection from gender-based violence\n\nFile complaints with the National/State Women Commission for violations.',
'Constitution, Equal Remuneration Act'),

(4, 'Right to Protection from Harassment', 
'Women have the right to work and live free from sexual harassment.',
'The law provides:\n\n1. Internal Complaints Committee (ICC) in workplaces\n2. Protection from sexual harassment\n3. Right to file complaints\n4. Confidentiality of proceedings\n5. Protection from retaliation\n\nEvery workplace with 10+ employees must have an ICC. File complaints immediately.',
'Sexual Harassment of Women at Workplace Act, 2013'),

(5, 'Right to Privacy Online', 
'Individuals have the right to privacy and protection of personal data online.',
'Your digital privacy rights include:\n\n1. Control over personal data\n2. Right to be forgotten\n3. Data breach notifications\n4. Consent for data collection\n5. Access to your data\n\nReport violations to data protection authorities or cyber cells.',
'IT Act, Data Protection Laws'),

(5, 'Right to Report Cybercrimes', 
'Citizens have the right to report cybercrimes and seek legal action.',
'You can report:\n\n1. Hacking and identity theft\n2. Online fraud and phishing\n3. Cyberbullying and harassment\n4. Revenge porn and morphing\n5. Financial frauds\n\nFile complaints at www.cybercrime.gov.in or nearest cyber cell. Keep all digital evidence.',
'IT Act, 2000 and amendments');

-- Insert sample document templates
INSERT INTO document_templates (template_name, template_type, template_content, fields_json) VALUES
('Rental Agreement', 'RENTAL', 
'RENTAL AGREEMENT

This Rental Agreement is made on {{DATE}} between:

LANDLORD: {{LANDLORD_NAME}}
Address: {{LANDLORD_ADDRESS}}

AND

TENANT: {{TENANT_NAME}}
Address: {{TENANT_ADDRESS}}

PROPERTY DETAILS:
The landlord agrees to rent the property located at:
{{PROPERTY_ADDRESS}}

TERMS AND CONDITIONS:

1. RENT: The monthly rent is {{RENT_AMOUNT}} payable on or before {{PAYMENT_DATE}} of each month.

2. SECURITY DEPOSIT: The tenant has paid a security deposit of {{DEPOSIT_AMOUNT}}.

3. DURATION: This agreement is valid from {{START_DATE}} to {{END_DATE}}.

4. MAINTENANCE: The tenant agrees to maintain the property in good condition.

5. TERMINATION: Either party may terminate this agreement with {{NOTICE_PERIOD}} days written notice.

6. UTILITIES: {{UTILITIES_CLAUSE}}

LANDLORD SIGNATURE: ________________
Date: {{DATE}}

TENANT SIGNATURE: ________________
Date: {{DATE}}

WITNESS 1: ________________
Name: {{WITNESS1_NAME}}

WITNESS 2: ________________
Name: {{WITNESS2_NAME}}',
'["DATE","LANDLORD_NAME","LANDLORD_ADDRESS","TENANT_NAME","TENANT_ADDRESS","PROPERTY_ADDRESS","RENT_AMOUNT","PAYMENT_DATE","DEPOSIT_AMOUNT","START_DATE","END_DATE","NOTICE_PERIOD","UTILITIES_CLAUSE","WITNESS1_NAME","WITNESS2_NAME"]'),

('Complaint Letter', 'COMPLAINT', 
'COMPLAINT LETTER

Date: {{DATE}}

To,
{{RECIPIENT_NAME}}
{{RECIPIENT_DESIGNATION}}
{{RECIPIENT_ADDRESS}}

Subject: {{SUBJECT}}

Dear Sir/Madam,

I, {{SENDER_NAME}}, residing at {{SENDER_ADDRESS}}, would like to bring to your attention the following matter:

COMPLAINT DETAILS:
{{COMPLAINT_DESCRIPTION}}

INCIDENT DATE: {{INCIDENT_DATE}}
LOCATION: {{INCIDENT_LOCATION}}

I have faced the following issues:
{{ISSUES_LIST}}

I request you to:
{{REQUESTED_ACTION}}

I have attached the following documents as evidence:
{{ATTACHMENTS_LIST}}

I hope for a prompt resolution of this matter. Please contact me at {{CONTACT_NUMBER}} or {{EMAIL}} for any clarifications.

Thank you for your attention to this matter.

Yours sincerely,

{{SENDER_NAME}}
Signature: ________________
Date: {{DATE}}',
'["DATE","RECIPIENT_NAME","RECIPIENT_DESIGNATION","RECIPIENT_ADDRESS","SUBJECT","SENDER_NAME","SENDER_ADDRESS","COMPLAINT_DESCRIPTION","INCIDENT_DATE","INCIDENT_LOCATION","ISSUES_LIST","REQUESTED_ACTION","ATTACHMENTS_LIST","CONTACT_NUMBER","EMAIL"]'),

('Simple Will', 'WILL', 
'LAST WILL AND TESTAMENT

I, {{TESTATOR_NAME}}, residing at {{TESTATOR_ADDRESS}}, being of sound mind and memory, do hereby make this my Last Will and Testament.

1. REVOCATION: I hereby revoke all previous wills and codicils made by me.

2. EXECUTOR: I appoint {{EXECUTOR_NAME}} of {{EXECUTOR_ADDRESS}} as the Executor of this Will.

3. BENEFICIARIES AND BEQUESTS:

{{BEQUESTS_LIST}}

4. RESIDUARY ESTATE: All remaining property and assets shall be distributed to {{RESIDUARY_BENEFICIARY}}.

5. GUARDIANSHIP: If applicable, I appoint {{GUARDIAN_NAME}} as guardian of my minor children.

6. DEBTS: All my just debts and funeral expenses shall be paid from my estate.

IN WITNESS WHEREOF, I have set my hand this {{DATE}}.

TESTATOR SIGNATURE: ________________
Name: {{TESTATOR_NAME}}
Date: {{DATE}}

WITNESS 1: ________________
Name: {{WITNESS1_NAME}}
Address: {{WITNESS1_ADDRESS}}
Date: {{DATE}}

WITNESS 2: ________________
Name: {{WITNESS2_NAME}}
Address: {{WITNESS2_ADDRESS}}
Date: {{DATE}}',
'["TESTATOR_NAME","TESTATOR_ADDRESS","EXECUTOR_NAME","EXECUTOR_ADDRESS","BEQUESTS_LIST","RESIDUARY_BENEFICIARY","GUARDIAN_NAME","DATE","WITNESS1_NAME","WITNESS1_ADDRESS","WITNESS2_NAME","WITNESS2_ADDRESS"]');
