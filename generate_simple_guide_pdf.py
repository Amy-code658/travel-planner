#!/usr/bin/env python3
"""
Simple, Crystal-Clear Guide to the Personalized Tourism Management System
Generates an easy-to-understand, beautifully formatted PDF in plain English.
"""

import sys
import os
from reportlab.lib import colors
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak, KeepTogether, HRFlowable
)
from reportlab.pdfgen import canvas

PAGE_WIDTH, PAGE_HEIGHT = A4
MARGIN = 40
CONTENT_WIDTH = PAGE_WIDTH - (2 * MARGIN)

# Friendly Theme Palette
PRIMARY = colors.HexColor("#E05638")       # Warm Sunset Coral
PRIMARY_DARK = colors.HexColor("#B2361C")  # Terracotta
ACCENT_GREEN = colors.HexColor("#27AE60")  # Friendly Green
TEXT_DARK = colors.HexColor("#2D3748")     # Soft Charcoal
TEXT_MUTED = colors.HexColor("#718096")    # Slate Gray
BG_LIGHT = colors.HexColor("#FFF5F3")      # Light warm tint
BORDER_COLOR = colors.HexColor("#E2E8F0")  # Soft line
WHITE = colors.HexColor("#FFFFFF")
CARD_BG = colors.HexColor("#F8FAFC")
HEADER_BG = colors.HexColor("#B2361C")

class NumberedCanvas(canvas.Canvas):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self._saved_page_states = []

    def showPage(self):
        self._saved_page_states.append(dict(self.__dict__))
        self._startPage()

    def save(self):
        num_pages = len(self._saved_page_states)
        for state in self._saved_page_states:
            self.__dict__.update(state)
            self.draw_header_footer(num_pages)
            super().showPage()
        super().save()

    def draw_header_footer(self, total_pages):
        if self._pageNumber == 1:
            return
        self.saveState()
        self.setFont("Helvetica-Bold", 8)
        self.setFillColor(PRIMARY_DARK)
        self.drawString(MARGIN, PAGE_HEIGHT - 28, "TRAVEL PLANNER APP - SIMPLE GUIDE")
        self.setFont("Helvetica", 8)
        self.setFillColor(TEXT_MUTED)
        self.drawRightString(PAGE_WIDTH - MARGIN, PAGE_HEIGHT - 28, "Made Simple & Easy to Understand")

        self.setStrokeColor(BORDER_COLOR)
        self.setLineWidth(0.75)
        self.line(MARGIN, PAGE_HEIGHT - 32, PAGE_WIDTH - MARGIN, PAGE_HEIGHT - 32)

        self.line(MARGIN, 36, PAGE_WIDTH - MARGIN, 36)
        self.setFont("Helvetica", 8)
        self.setFillColor(TEXT_MUTED)
        self.drawString(MARGIN, 24, "Personalized Tourism Management System | Easy Reference")
        page_str = f"Page {self._pageNumber} of {total_pages}"
        self.drawRightString(PAGE_WIDTH - MARGIN, 24, page_str)
        self.restoreState()

def build_pdf(filename):
    doc = SimpleDocTemplate(
        filename,
        pagesize=A4,
        leftMargin=MARGIN,
        rightMargin=MARGIN,
        topMargin=MARGIN + 10,
        bottomMargin=MARGIN + 10
    )

    styles = getSampleStyleSheet()

    title_style = ParagraphStyle(
        'DocTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=22,
        leading=26,
        textColor=PRIMARY_DARK,
        spaceAfter=6
    )

    subtitle_style = ParagraphStyle(
        'DocSubTitle',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=12,
        leading=16,
        textColor=TEXT_MUTED,
        spaceAfter=14
    )

    h1_style = ParagraphStyle(
        'Heading1',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=14,
        leading=18,
        textColor=PRIMARY_DARK,
        spaceBefore=14,
        spaceAfter=6,
        keepWithNext=True
    )

    h2_style = ParagraphStyle(
        'Heading2',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=11,
        leading=15,
        textColor=TEXT_DARK,
        spaceBefore=8,
        spaceAfter=4,
        keepWithNext=True
    )

    body_style = ParagraphStyle(
        'Body',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=9.5,
        leading=14,
        textColor=TEXT_DARK,
        spaceAfter=6
    )

    bullet_style = ParagraphStyle(
        'Bullet',
        parent=body_style,
        leftIndent=15,
        firstLineIndent=-10,
        spaceAfter=4
    )

    box_text = ParagraphStyle(
        'BoxText',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=9,
        leading=13.5,
        textColor=TEXT_DARK
    )

    table_header = ParagraphStyle(
        'TableHeader',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=8.5,
        leading=11,
        textColor=WHITE
    )

    table_cell = ParagraphStyle(
        'TableCell',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=8.5,
        leading=11.5,
        textColor=TEXT_DARK
    )

    table_cell_bold = ParagraphStyle(
        'TableCellBold',
        parent=table_cell,
        fontName='Helvetica-Bold'
    )

    story = []

    # Title & Banner
    story.append(Spacer(1, 10))
    story.append(Paragraph("Personalized Tourism Management System", title_style))
    story.append(Paragraph("A Simple, No-Jargon Guide: How It Works, Who Built What, and What Every File Does", subtitle_style))

    summary_box = """<b>What is this project in 1 sentence?</b><br/>
It is a desktop app (like a mini MakeMyTrip or Expedia) built with Java and MySQL where people can search holiday trips, get smart recommendations based on their budget, book tickets, download PDF invoices, and see airline boarding passes, while an admin manages the trips and looks at sales graphs."""
    
    t_sum = Table([[Paragraph(summary_box, box_text)]], colWidths=[CONTENT_WIDTH])
    t_sum.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), BG_LIGHT),
        ('BOX', (0,0), (-1,-1), 1, PRIMARY),
        ('PADDING', (0,0), (-1,-1), 8),
    ]))
    story.append(t_sum)
    story.append(Spacer(1, 12))

    # SECTION 1: THE BIG PICTURE
    story.append(Paragraph("1. The Big Picture: How Does the Code Work?", h1_style))
    story.append(Paragraph(
        "Think of the application as a restaurant with three main teams working together:",
        body_style
    ))

    restaurant_analogy = """<b>1. The Waiter (The UI - User Interface):</b><br/>
These are the screens and buttons the user sees (the login window, the cards showing pictures of Bali and Paris, the booking pop-up). The UI doesn't do math or touch the database directly; it just takes orders from the user and hands them to the kitchen.<br/><br/>
<b>2. The Chef (The Services - Business Logic):</b><br/>
This is the brain of the app. It checks: Is the user's password correct? Does the trip match their budget? How much is 80% if they cancel? Is their credit card valid? It does all the thinking and calculations.<br/><br/>
<b>3. The Store Room (The DAOs & Database):</b><br/>
DAO stands for 'Data Access Object'. These files act like storekeepers. Whenever the Chef needs recipes or ingredients (like trip details or customer accounts), the DAO runs a secure SQL command to pull it from or save it to MySQL."""
    
    t_ana = Table([[Paragraph(restaurant_analogy, box_text)]], colWidths=[CONTENT_WIDTH])
    t_ana.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), CARD_BG),
        ('BOX', (0,0), (-1,-1), 1, BORDER_COLOR),
        ('PADDING', (0,0), (-1,-1), 8),
    ]))
    story.append(t_ana)
    story.append(Spacer(1, 10))

    # SECTION 2: THE LOGIC BEHIND IT
    story.append(Paragraph("2. The Logic Behind the Application (In Simple Terms)", h1_style))
    story.append(Paragraph("Here are the 4 main logical rules that power the application:", body_style))

    # Rule 1
    story.append(Paragraph("Rule 1: How the Smart Recommendation Engine Works (Gowri P)", h2_style))
    story.append(Paragraph(
        "When a user says: <i>'I have Rs. 50,000, I want a 5-day Beach holiday for 2 people'</i>, the app looks at all trips in the database and gives each trip a match score between <b>0% and 100%</b> using three questions:",
        body_style
    ))
    rec_rules = [
        "<b>Does it fit your budget? (40% weight):</b> It calculates the total cost (ticket price + daily food/stay allowance). If it fits your budget, you get full points. If it's too expensive, points are deducted. If it is more than 30% over your budget, this score drops to 0.",
        "<b>Does it fit your time? (35% weight):</b> If you asked for 5 days and the package is 5 days, you get 100%. If the package is 4 days or 6 days, you get partial points. If it's way too long or short, points are reduced.",
        "<b>Does it match what you like? (25% weight):</b> If you like 'Beach' and the trip is Goa or Bali, you get 100% for this test. If it's a Mountain trip, you get only 15%."
    ]
    for r in rec_rules:
        story.append(Paragraph(f"&bull; {r}", bullet_style))
    story.append(Paragraph("The app adds up the weighted scores, sorts the trips from highest to lowest, and shows the best ones first!", body_style))
    story.append(Spacer(1, 6))

    # Rule 2
    story.append(Paragraph("Rule 2: How Prices & Refunds Work (Garry George)", h2_style))
    price_rules = [
        "<b>Calculating Total Price:</b> <code>Total = (Ticket Price x Number of Guests) - Any Discount</code>. For example, if a trip to Bali is Rs. 10,000 per person with a 10% discount, 2 people pay Rs. 18,000 instead of Rs. 20,000.",
        "<b>Cancellation & Refund Rule:</b> If a customer cancels their booking <b>more than 48 hours (2 days)</b> before the trip starts, they automatically get an <b>80% refund</b>. If they cancel less than 48 hours before, or after the trip has already started, they get <b>0% refund</b>."
    ]
    for pr in price_rules:
        story.append(Paragraph(f"&bull; {pr}", bullet_style))
    story.append(Spacer(1, 6))

    # Rule 3
    story.append(Paragraph("Rule 3: Password Security (Amy)", h2_style))
    story.append(Paragraph(
        "When you type a password like <code>secret123</code>, the app <b>never</b> saves <code>secret123</code> in the database. "
        "Instead, it scrambles it using a tool called <b>BCrypt</b> into a 60-character scrambled code (e.g. <code>$2a$12$ARX4KA...</code>). "
        "Even if an attacker stole the entire database, they would never be able to read anyone's real password.",
        body_style
    ))
    story.append(Spacer(1, 6))

    # Rule 4
    story.append(Paragraph("Rule 4: Multi-Currency & Digital Boarding Pass (Amy)", h2_style))
    story.append(Paragraph(
        "The app has a currency dropdown (USD $, EUR &euro;, GBP &pound;, INR &#8377;, JPY &yen;). When you change the currency, every card on the screen instantly recalculates its price into that currency without having to refresh or restart. "
        "Also, when you confirm a trip, you can open a digital boarding pass that looks like a real flight ticket with a seat number, gate, and drawn barcode.",
        body_style
    ))
    story.append(Spacer(1, 10))

    # SECTION 3: WHO DID WHAT
    story.append(Paragraph("3. Who Built What? (Team Work Breakdown)", h1_style))
    story.append(Paragraph("Here is what every member of the project team did, in plain everyday terms:", body_style))

    dev_data = [
        [
            Paragraph("Team Member", table_header),
            Paragraph("What They Were Responsible For", table_header),
            Paragraph("What They Actually Built", table_header)
        ],
        [
            Paragraph("<b>Amy</b><br/>(Project Lead & Architect)", table_cell_bold),
            Paragraph("Set up the foundation, connected everyone's code, built user login, security, and main dashboard.", table_cell),
            Paragraph(
                "&bull; Created the project skeleton and database schema (6 tables).<br/>"
                "&bull; Built the Login and Register screens with password encryption.<br/>"
                "&bull; Built the main windows for Customer and Admin.<br/>"
                "&bull; Styled the whole app in warm sunset terracotta colors.<br/>"
                "&bull; Added real-time currency conversion (USD, INR, EUR, etc.).<br/>"
                "&bull; Created the digital airline boarding pass with barcodes.<br/>"
                "&bull; Reviewed everyone's code and merged all pull requests.",
                table_cell
            )
        ],
        [
            Paragraph("<b>Aein Shaji</b><br/>(Dev 3)", table_cell_bold),
            Paragraph("Trip Catalog & Package Cards", table_cell),
            Paragraph(
                "&bull; Created the screen where users browse all travel packages.<br/>"
                "&bull; Built the filter buttons (Beach, Adventure, Culture, Nature, Luxury).<br/>"
                "&bull; Designed the travel cards showing photos, trip duration, price, and the 'Book Now' button.",
                table_cell
            )
        ],
        [
            Paragraph("<b>Gowri P</b><br/>(Dev 4)", table_cell_bold),
            Paragraph("Smart Recommendations & Day-by-Day Itinerary", table_cell),
            Paragraph(
                "&bull; Created the Recommendation algorithm that calculates a 0-100% match score.<br/>"
                "&bull; Built the survey screen where users input their budget, days, and travel style.<br/>"
                "&bull; Created the popup window showing Day 1, Day 2, Day 3 activity schedules for each trip.",
                table_cell
            )
        ],
        [
            Paragraph("<b>Garry George</b><br/>(Dev 5)", table_cell_bold),
            Paragraph("Booking, Payments & Cancellation", table_cell),
            Paragraph(
                "&bull; Built the 'Book Trip' popup window with date picker and guest counter.<br/>"
                "&bull; Created the Credit Card and UPI payment options.<br/>"
                "&bull; Programmed the 48-hour 80% refund logic when someone cancels.<br/>"
                "&bull; Built the 'My Bookings' screen where users see their booked trips.",
                table_cell
            )
        ],
        [
            Paragraph("<b>Pius Varghese</b><br/>(Dev 6)", table_cell_bold),
            Paragraph("Admin Inventory & Booking Oversight", table_cell),
            Paragraph(
                "&bull; Built the Admin screen where managers can Add, Edit, or Delete trips.<br/>"
                "&bull; Built the master table where admins can see every booking made by every customer.",
                table_cell
            )
        ],
        [
            Paragraph("<b>Ashwin V</b><br/>(Dev 7)", table_cell_bold),
            Paragraph("PDF Downloads & Admin Sales Charts", table_cell),
            Paragraph(
                "&bull; Integrated the PDF generator to download Itineraries and Tax Invoices.<br/>"
                "&bull; Built the Admin Reports screen with colorful bar charts and pie charts showing top destinations and monthly revenue.",
                table_cell
            )
        ],
        [
            Paragraph("<b>Atul</b><br/>(Dev 8)", table_cell_bold),
            Paragraph("Reviews & Trip Alerts", table_cell),
            Paragraph(
                "&bull; Built the 5-star rating and review popup for customers.<br/>"
                "&bull; Built the alert banner on the customer dashboard reminding them if their trip is coming up in the next 7 days.",
                table_cell
            )
        ]
    ]

    t_dev = Table(dev_data, colWidths=[CONTENT_WIDTH*0.22, CONTENT_WIDTH*0.28, CONTENT_WIDTH*0.50])
    t_dev.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), HEADER_BG),
        ('ALIGN', (0,0), (-1,-1), 'LEFT'),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('GRID', (0,0), (-1,-1), 0.5, BORDER_COLOR),
        ('PADDING', (0,0), (-1,-1), 5),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [WHITE, CARD_BG])
    ]))
    story.append(t_dev)
    story.append(Spacer(1, 10))

    # SECTION 4: WHAT EACH FILE DOES
    story.append(Paragraph("4. What Does Each File Do? (Simple Cheat Sheet)", h1_style))
    story.append(Paragraph(
        "Here is what each file in the project folder does, grouped by folder:",
        body_style
    ))

    files_data = [
        [
            Paragraph("Folder & File Name", table_header),
            Paragraph("What It Does In Plain Words", table_header)
        ],
        # Config
        [Paragraph("<b>config / DBConnection.java</b>", table_cell_bold), Paragraph("Connects Java to MySQL. Ensures the app uses one shared connection instead of opening hundreds of connections.", table_cell)],
        [Paragraph("<b>config / ThemeConstants.java</b>", table_cell_bold), Paragraph("The stylebook. Stores the colors (warm terracotta, coral, white), fonts, and button sizes so the whole app looks consistent.", table_cell)],
        [Paragraph("<b>config / CurrencyManager.java</b>", table_cell_bold), Paragraph("Handles money conversion (USD, INR, EUR, GBP, JPY) so prices change live on screen.", table_cell)],
        [Paragraph("<b>config / LanguageManager.java</b>", table_cell_bold), Paragraph("Handles translations (English, Spanish, French, German, Hindi, Japanese).", table_cell)],
        
        # Models
        [Paragraph("<b>model / User.java</b>", table_cell_bold), Paragraph("A box holding user information: user ID, full name, email, role (Customer or Admin), and phone.", table_cell)],
        [Paragraph("<b>model / Destination.java</b>", table_cell_bold), Paragraph("A box holding destination details: city name, country, description, and daily spending cost.", table_cell)],
        [Paragraph("<b>model / Package.java</b>", table_cell_bold), Paragraph("A box holding trip package details: title, number of days, price, and discount.", table_cell)],
        [Paragraph("<b>model / ItineraryDay.java</b>", table_cell_bold), Paragraph("A box holding one day's schedule (e.g. Day 1: Arrive and hotel check-in).", table_cell)],
        [Paragraph("<b>model / Booking.java</b>", table_cell_bold), Paragraph("A box holding a booked ticket: travel date, guests, total amount paid, and status (CONFIRMED or CANCELLED).", table_cell)],
        [Paragraph("<b>model / Review.java</b>", table_cell_bold), Paragraph("A box holding customer feedback: 1 to 5 stars and written review.", table_cell)],
        [Paragraph("<b>model / CustomerBookingDossier.java</b>", table_cell_bold), Paragraph("A combined report used by admins that shows a customer's name, the trip they picked, and their masked payment info.", table_cell)],
        [Paragraph("<b>model / UserSession.java</b>", table_cell_bold), Paragraph("Remembers who is currently logged in so you don't have to re-enter your password on every screen.", table_cell)],

        # DAOs
        [Paragraph("<b>dao / UserDAO.java</b>", table_cell_bold), Paragraph("Saves new users to MySQL and finds users by email when they log in.", table_cell)],
        [Paragraph("<b>dao / DestinationDAO.java</b>", table_cell_bold), Paragraph("Fetches destination places (like Bali, Swiss Alps, Kyoto) from MySQL.", table_cell)],
        [Paragraph("<b>dao / PackageDAO.java</b>", table_cell_bold), Paragraph("Fetches all travel packages from MySQL and handles search filters.", table_cell)],
        [Paragraph("<b>dao / ItineraryDAO.java</b>", table_cell_bold), Paragraph("Fetches the Day 1, Day 2, Day 3 activity schedule for any trip package.", table_cell)],
        [Paragraph("<b>dao / BookingDAO.java</b>", table_cell_bold), Paragraph("Saves bookings into the database and marks them as cancelled if requested.", table_cell)],
        [Paragraph("<b>dao / ReviewDAO.java</b>", table_cell_bold), Paragraph("Saves customer reviews and calculates average ratings for each package.", table_cell)],
        [Paragraph("<b>dao / AdminDAO.java</b>", table_cell_bold), Paragraph("Runs special administrative queries to list all customer dossiers and payment states.", table_cell)],

        # Services
        [Paragraph("<b>service / AuthService.java</b>", table_cell_bold), Paragraph("Checks if the entered email and password match, and scrambles new passwords with BCrypt.", table_cell)],
        [Paragraph("<b>service / RecommendationService.java</b>", table_cell_bold), Paragraph("The matching brain. Scores packages from 0% to 100% based on user budget, duration, and preferences.", table_cell)],
        [Paragraph("<b>service / ItineraryService.java</b>", table_cell_bold), Paragraph("Retrieves and arranges the day-wise itinerary activities in order.", table_cell)],
        [Paragraph("<b>service / BookingService.java</b>", table_cell_bold), Paragraph("Checks booking dates, calculates final price, checks payment, and calculates the 80% refund if cancelled early.", table_cell)],
        [Paragraph("<b>service / CardPayment.java</b>", table_cell_bold), Paragraph("Checks credit card numbers (must be 16 digits), checks expiration date, and hides all but the last 4 digits for safety.", table_cell)],
        [Paragraph("<b>service / UpiPayment.java</b>", table_cell_bold), Paragraph("Checks UPI IDs (must be in the format name@bank) and generates a payment confirmation ID.", table_cell)],
        [Paragraph("<b>service / PDFExportService.java</b>", table_cell_bold), Paragraph("Builds downloadable PDF files for the travel itinerary and the official tax invoice.", table_cell)],
        [Paragraph("<b>service / AnalyticsService.java</b>", table_cell_bold), Paragraph("Calculates total revenue and most popular destinations so the admin can see graphs.", table_cell)],

        # UI Screens
        [Paragraph("<b>Main.java</b>", table_cell_bold), Paragraph("The starting point of the app. Starts the FlatLaf modern theme, checks database connection, and opens the Login screen.", table_cell)],
        [Paragraph("<b>ui / auth / LoginFrame.java</b>", table_cell_bold), Paragraph("The login screen with email, password, and links to register.", table_cell)],
        [Paragraph("<b>ui / auth / RegisterFrame.java</b>", table_cell_bold), Paragraph("The registration screen for new customers to create an account.", table_cell)],
        [Paragraph("<b>ui / customer / CustomerMainFrame.java</b>", table_cell_bold), Paragraph("The main dashboard for travelers with tabs for Catalog, Recommendations, and My Bookings.", table_cell)],
        [Paragraph("<b>ui / customer / CatalogPanel.java</b>", table_cell_bold), Paragraph("The trip browsing screen with search bar and category filter chips.", table_cell)],
        [Paragraph("<b>ui / customer / PackageCard.java</b>", table_cell_bold), Paragraph("The visual card showing trip image, live weather badge, star rating, price, and 'Book Now' button.", table_cell)],
        [Paragraph("<b>ui / customer / RecommendationsPanel.java</b>", table_cell_bold), Paragraph("The preferences survey where users select their budget and get matched trips.", table_cell)],
        [Paragraph("<b>ui / customer / ItineraryDialog.java</b>", table_cell_bold), Paragraph("The popup window showing the day-by-day plan with a 'Download PDF' button.", table_cell)],
        [Paragraph("<b>ui / customer / BookingDialog.java</b>", table_cell_bold), Paragraph("The popup window where customers choose their travel date, guests, and pay via Card or UPI.", table_cell)],
        [Paragraph("<b>ui / customer / BookingHistoryPanel.java</b>", table_cell_bold), Paragraph("The screen listing your booked trips with buttons to cancel, download invoice, or get a boarding pass.", table_cell)],
        [Paragraph("<b>ui / customer / BoardingPassDialog.java</b>", table_cell_bold), Paragraph("A stylish digital flight ticket with gate number, seat assignment, and barcode.", table_cell)],
        [Paragraph("<b>ui / customer / ReviewDialog.java</b>", table_cell_bold), Paragraph("The popup window where customers give 1 to 5 stars and leave comments.", table_cell)],
        [Paragraph("<b>ui / admin / AdminMainFrame.java</b>", table_cell_bold), Paragraph("The main dashboard for the system administrator.", table_cell)],
        [Paragraph("<b>ui / admin / InventoryManagementPanel.java</b>", table_cell_bold), Paragraph("The admin screen to Add, Edit, or Delete trips in the database.", table_cell)],
        [Paragraph("<b>ui / admin / BookingOversightPanel.java</b>", table_cell_bold), Paragraph("The master table where admins monitor all bookings made by customers.", table_cell)],
        [Paragraph("<b>ui / admin / CustomerBookingsAdminPanel.java</b>", table_cell_bold), Paragraph("Admin table showing customers, their chosen trips, and masked payment settlements.", table_cell)],
        [Paragraph("<b>ui / admin / CustomerDossierDialog.java</b>", table_cell_bold), Paragraph("Admin popup showing a detailed profile and booking history of a specific customer.", table_cell)],
        [Paragraph("<b>ui / admin / ReportsPanel.java</b>", table_cell_bold), Paragraph("The analytics screen showing colorful JFreeChart bar graphs and revenue charts.", table_cell)],

        # Database script
        [Paragraph("<b>resources / tourism_schema.sql</b>", table_cell_bold), Paragraph("The SQL script that creates all 6 tables and fills them with default trips for Bali, Paris, Swiss Alps, Kyoto, Manali, Goa, Shimla, Kerala, Vietnam, Maldives, and Egypt.", table_cell)]
    ]

    t_files = Table(files_data, colWidths=[CONTENT_WIDTH*0.35, CONTENT_WIDTH*0.65])
    t_files.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), HEADER_BG),
        ('ALIGN', (0,0), (-1,-1), 'LEFT'),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('GRID', (0,0), (-1,-1), 0.5, BORDER_COLOR),
        ('PADDING', (0,0), (-1,-1), 4),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [WHITE, CARD_BG])
    ]))
    story.append(t_files)
    story.append(Spacer(1, 12))

    # SECTION 5: HOW TO RUN
    story.append(Paragraph("5. How to Run the App (Quick Steps)", h1_style))
    run_steps = """<b>Step 1:</b> Start MySQL on your computer.<br/>
<b>Step 2:</b> Open terminal in the project folder and run:<br/>
&nbsp;&nbsp;&nbsp;&nbsp;<code>mvn clean compile</code><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<code>mvn exec:java</code><br/>
<b>Step 3:</b> Log in with either of the test accounts:<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&bull; <b>Customer Account:</b> Email: <code>john@gmail.com</code> | Password: <code>user123</code><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&bull; <b>Admin Account:</b> Email: <code>admin@tourism.com</code> | Password: <code>admin123</code>"""
    
    t_run = Table([[Paragraph(run_steps, box_text)]], colWidths=[CONTENT_WIDTH])
    t_run.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), BG_LIGHT),
        ('BOX', (0,0), (-1,-1), 1, ACCENT_GREEN),
        ('PADDING', (0,0), (-1,-1), 8),
    ]))
    story.append(t_run)

    story.append(Spacer(1, 14))
    story.append(HRFlowable(width="100%", thickness=1, color=PRIMARY, spaceBefore=8, spaceAfter=8))
    story.append(Paragraph(
        "<b>Personalized Tourism Management System</b> &mdash; Easy Reference Guide",
        ParagraphStyle('FooterNotice', parent=body_style, fontSize=8, textColor=TEXT_MUTED, alignment=1)
    ))

    doc.build(story, canvasmaker=NumberedCanvas)
    print(f"Successfully generated: {filename}")

if __name__ == '__main__':
    output_pdf = "Personalized_Tourism_Management_System_Simple_Guide.pdf"
    if len(sys.argv) > 1:
        output_pdf = sys.argv[1]
    build_pdf(output_pdf)
