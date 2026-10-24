#!/usr/bin/env python3
"""
Personalized Tourism Management System - Comprehensive Code & Architecture Guide PDF Generator
Uses ReportLab 5.x to produce an authoritative, beautifully designed technical report.
"""

import sys
import os
from reportlab.lib import colors
from reportlab.lib.pagesizes import A4
from reportlab.lib.units import inch
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak, KeepTogether, HRFlowable
)
from reportlab.pdfgen import canvas

# Dimensions
PAGE_WIDTH, PAGE_HEIGHT = A4
MARGIN = 40
CONTENT_WIDTH = PAGE_WIDTH - (2 * MARGIN)

# Theme Palette (matching application's ThemeConstants)
PRIMARY = colors.HexColor("#E05638")       # Warm Sunset Coral
PRIMARY_DARK = colors.HexColor("#B2361C")  # Deep Terracotta
ACCENT = colors.HexColor("#27AE60")        # Emerald Green
TEXT_DARK = colors.HexColor("#1A202C")     # Charcoal Dark
TEXT_MUTED = colors.HexColor("#4A5568")    # Slate Gray
BG_LIGHT = colors.HexColor("#F8FAFC")      # Soft Off-White
BORDER_COLOR = colors.HexColor("#E2E8F0")  # Subtle Gray
WHITE = colors.HexColor("#FFFFFF")
CARD_BG = colors.HexColor("#F1F5F9")
CODE_BG = colors.HexColor("#2D3748")
CODE_TEXT = colors.HexColor("#F7FAFC")
ACCENT_AMBER = colors.HexColor("#D97706")
ACCENT_BLUE = colors.HexColor("#2563EB")


class NumberedCanvas(canvas.Canvas):
    """
    Two-pass canvas to dynamically compute and draw total page count,
    running headers, and running footers.
    """
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
        # Skip headers and footers on cover page (page 1)
        if self._pageNumber == 1:
            return

        self.saveState()

        # Running Header
        self.setFont("Helvetica-Bold", 8)
        self.setFillColor(PRIMARY_DARK)
        self.drawString(MARGIN, PAGE_HEIGHT - 28, "PERSONALIZED TOURISM MANAGEMENT SYSTEM")
        self.setFont("Helvetica", 8)
        self.setFillColor(TEXT_MUTED)
        self.drawRightString(PAGE_WIDTH - MARGIN, PAGE_HEIGHT - 28, "Architecture & Code Reference Guide")

        self.setStrokeColor(BORDER_COLOR)
        self.setLineWidth(0.75)
        self.line(MARGIN, PAGE_HEIGHT - 32, PAGE_WIDTH - MARGIN, PAGE_HEIGHT - 32)

        # Running Footer
        self.line(MARGIN, 36, PAGE_WIDTH - MARGIN, 36)
        self.setFont("Helvetica", 8)
        self.setFillColor(TEXT_MUTED)
        self.drawString(MARGIN, 24, "Confidential - Project Technical Documentation - Team Work Breakdown")
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

    # Custom Typography Hierarchy
    title_style = ParagraphStyle(
        'DocTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=24,
        leading=28,
        textColor=PRIMARY_DARK,
        alignment=0,
        spaceAfter=6
    )

    subtitle_style = ParagraphStyle(
        'DocSubTitle',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=12,
        leading=16,
        textColor=TEXT_MUTED,
        alignment=0,
        spaceAfter=15
    )

    h1_style = ParagraphStyle(
        'Heading1_Custom',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=16,
        leading=20,
        textColor=PRIMARY_DARK,
        spaceBefore=14,
        spaceAfter=8,
        keepWithNext=True
    )

    h2_style = ParagraphStyle(
        'Heading2_Custom',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=12,
        leading=16,
        textColor=TEXT_DARK,
        spaceBefore=10,
        spaceAfter=4,
        keepWithNext=True
    )

    body_style = ParagraphStyle(
        'Body_Custom',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=9.5,
        leading=13.5,
        textColor=TEXT_DARK,
        spaceAfter=6
    )

    body_bold = ParagraphStyle(
        'Body_Bold',
        parent=body_style,
        fontName='Helvetica-Bold'
    )

    bullet_style = ParagraphStyle(
        'Bullet_Custom',
        parent=body_style,
        leftIndent=15,
        firstLineIndent=-10,
        spaceAfter=4
    )

    code_style = ParagraphStyle(
        'Code_Custom',
        parent=styles['Normal'],
        fontName='Courier',
        fontSize=8,
        leading=11,
        textColor=CODE_TEXT
    )

    table_header = ParagraphStyle(
        'TableHeader',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=8.5,
        leading=11,
        textColor=WHITE,
        alignment=0
    )

    table_cell = ParagraphStyle(
        'TableCell',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=8,
        leading=11,
        textColor=TEXT_DARK
    )

    table_cell_bold = ParagraphStyle(
        'TableCellBold',
        parent=table_cell,
        fontName='Helvetica-Bold'
    )

    table_cell_code = ParagraphStyle(
        'TableCellCode',
        parent=styles['Normal'],
        fontName='Courier',
        fontSize=7.5,
        leading=10,
        textColor=PRIMARY_DARK
    )

    callout_text = ParagraphStyle(
        'CalloutText',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=9,
        leading=13,
        textColor=TEXT_DARK
    )

    story = []

    # =========================================================================
    # 1. COVER PAGE / HEADER HERO
    # =========================================================================
    story.append(Spacer(1, 20))
    story.append(Paragraph("Personalized Tourism Management System", title_style))
    story.append(Paragraph("Comprehensive Technical Reference Manual, Software Architecture, Algorithmic Logic & File-by-File Blueprint", subtitle_style))
    
    # Hero Callout Box
    meta_table_data = [
        [
            Paragraph("<b>Stack:</b> Java 17 LTS | Swing + FlatLaf | MySQL 8.0 | JDBC | OpenPDF | JFreeChart | jBCrypt", callout_text),
            Paragraph("<b>Architecture:</b> Layered MVC / DAO / Service Architecture", callout_text)
        ],
        [
            Paragraph("<b>Team Composition:</b> 7 Engineers (Dev 1 through Dev 8 roles)", callout_text),
            Paragraph("<b>Active Repository:</b> 48 Java Classes, 6 Database Tables, 11 Curated Destinations", callout_text)
        ]
    ]
    meta_table = Table(meta_table_data, colWidths=[CONTENT_WIDTH*0.5, CONTENT_WIDTH*0.5])
    meta_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), CARD_BG),
        ('BOX', (0,0), (-1,-1), 1, BORDER_COLOR),
        ('PADDING', (0,0), (-1,-1), 8),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
    ]))
    story.append(meta_table)
    story.append(Spacer(1, 14))

    # =========================================================================
    # 2. EXECUTIVE SUMMARY & SYSTEM ARCHITECTURE
    # =========================================================================
    story.append(Paragraph("1. Executive Summary & Architectural Overview", h1_style))
    story.append(Paragraph(
        "The <b>Personalized Tourism Management System</b> is a production-grade enterprise desktop application engineered "
        "using <b>Java 17 (LTS)</b> and modern <b>Swing (FlatLaf)</b>. It delivers an intelligent travel planning platform featuring "
        "rule-based package recommendations, automated multi-day itinerary generation, dynamic multi-currency conversions, "
        "step-by-step booking with dual-mode payment gateways (Credit Card & UPI), cancellation with automated refund estimation, "
        "cryptographic user authentication (jBCrypt), digital boarding pass generation, downloadable OpenPDF itineraries/invoices, "
        "and an administrative portal with visual analytics powered by JFreeChart.",
        body_style
    ))

    story.append(Paragraph("Core Architectural Layers & Design Patterns", h2_style))
    arch_patterns = [
        "<b>Strict 3-Tier Layered Architecture:</b> Strict separation of concerns: Presentation Layer (<code>ui.*</code>) &rarr; Business Logic Layer (<code>service.*</code>) &rarr; Data Access Layer (<code>dao.*</code>) &rarr; Database (MySQL). UI classes are completely forbidden from executing raw SQL queries.",
        "<b>Singleton Pattern:</b> Implemented in <code>DBConnection</code> for thread-safe centralized database connection pooling, <code>UserSession</code> for tracking logged-in credentials across the desktop lifecycle, and <code>CurrencyManager</code> / <code>LanguageManager</code> for global locale synchronization.",
        "<b>Strategy Pattern:</b> Utilized in the payment subsystem where the <code>PaymentMethod</code> interface defines <code>processPayment()</code>, implemented concretely by <code>CardPayment</code> (Luhn-checked card validation, masking) and <code>UpiPayment</code> (Virtual Payment Address validation).",
        "<b>Observer / Listener Pattern:</b> Employed in <code>CurrencyManager</code> where UI panels register listeners to dynamically re-render package prices, striking previous prices and updating symbols upon currency switching without application restart.",
        "<b>Transfer Object / POJO Pattern:</b> Clean entity representation across all layers (<code>User</code>, <code>Destination</code>, <code>Package</code>, <code>Booking</code>, <code>ItineraryDay</code>, <code>Review</code>, <code>CustomerBookingDossier</code>)."
    ]
    for p in arch_patterns:
        story.append(Paragraph(f"&bull; {p}", bullet_style))

    story.append(Spacer(1, 8))

    # Architecture Flow Box
    arch_flow_text = """<b>Request Pipeline:</b><br/>
[Swing UI: Event Dispatch Thread (EDT)] &rarr; [Service Validation & Rules Engine] &rarr; [DAO PreparedStatements ('?' Bindings)] &rarr; [MySQL Database Engine]<br/>
[Data Display]: [MySQL ResultSets] &rarr; [Model POJOs] &rarr; [Service Aggregation/PDF/Charts] &rarr; [FlatLaf Custom Renderers]"""
    flow_table = Table([[Paragraph(arch_flow_text, callout_text)]], colWidths=[CONTENT_WIDTH])
    flow_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor("#EFF6FF")),
        ('BOX', (0,0), (-1,-1), 1, colors.HexColor("#BFDBFE")),
        ('PADDING', (0,0), (-1,-1), 8),
    ]))
    story.append(flow_table)

    story.append(Spacer(1, 14))

    # =========================================================================
    # 3. TEAM WORK BREAKDOWN (WHAT EACH DEV DID)
    # =========================================================================
    story.append(Paragraph("2. Team Work Breakdown & Developer Contributions", h1_style))
    story.append(Paragraph(
        "The project was executed by a collaborative engineering team with well-defined module ownership, "
        "formal Git feature branches, and Pull Request (PR) peer reviews merged into the <code>develop</code> branch. "
        "The table below documents what each developer delivered:",
        body_style
    ))

    dev_data = [
        [
            Paragraph("Developer & Role", table_header),
            Paragraph("Git Identity & Commits", table_header),
            Paragraph("Modules Owned & Specific Files Contributed", table_header),
            Paragraph("Key Responsibilities & Algorithmic Deliverables", table_header)
        ],
        [
            Paragraph("<b>Amy / Amy Ann Jims</b><br/>Team Lead & Full-Stack Architect", table_cell_bold),
            Paragraph("amyjims2103@gmail.com<br/>25 commits<br/>PR #1, #3-#6 Merges", table_cell),
            Paragraph(
                "&bull; <code>pom.xml</code>, <code>tourism_schema.sql</code><br/>"
                "&bull; <code>DBConnection.java</code>, <code>ThemeConstants.java</code><br/>"
                "&bull; <code>CurrencyManager.java</code>, <code>LanguageManager.java</code><br/>"
                "&bull; <code>UserDAO.java</code>, <code>AdminDAO.java</code>, <code>AuthService.java</code><br/>"
                "&bull; <code>LoginFrame.java</code>, <code>RegisterFrame.java</code><br/>"
                "&bull; <code>CustomerMainFrame.java</code>, <code>AdminMainFrame.java</code><br/>"
                "&bull; <code>BoardingPassDialog.java</code>, <code>CustomerBookingsAdminPanel.java</code><br/>"
                "&bull; <code>CustomerDossierDialog.java</code>, <code>Main.java</code>",
                table_cell
            ),
            Paragraph(
                "Foundational project scaffolding; database DDL schema and seed data for 11 global destinations; "
                "BCrypt user authentication and registration; central Swing UI wiring connecting all 8 developer modules; "
                "warm terracotta visual identity system; real-time dynamic multi-currency converter; "
                "digital boarding pass with custom ticket geometry; administrative customer roster & masked settlements inspector; "
                "headless JVM fallback and terminal diagnostics; code review and pull request integration.",
                table_cell
            )
        ],
        [
            Paragraph("<b>Aein Shaji</b><br/>Dev 3: Catalog & Browsing", table_cell_bold),
            Paragraph("aeinshaji16@gmail.com<br/>Commit <code>3a05858</code><br/>Branch: <code>feature/catalog</code>", table_cell),
            Paragraph(
                "&bull; <code>DestinationDAO.java</code><br/>"
                "&bull; <code>PackageDAO.java</code> (catalog queries)<br/>"
                "&bull; <code>CatalogPanel.java</code><br/>"
                "&bull; <code>PackageCard.java</code> (catalog layout)<br/>"
                "&bull; <code>CatalogMockPreview.java</code>",
                table_cell
            ),
            Paragraph(
                "Constructed travel package browsing catalog; implemented multi-attribute filtering (category chips, "
                "keyword search, price range); responsive card grid layout displaying destination hero photos, "
                "duration badges, discount indicators, and 'View Itinerary' / 'Book Now' trigger actions; "
                "mock preview harness for visual card testing.",
                table_cell
            )
        ],
        [
            Paragraph("<b>Gowri P</b><br/>Dev 4: Recommendation & Itinerary", table_cell_bold),
            Paragraph("gowripthampi@gmail.com<br/>Commit <code>3a0ef1e</code><br/>PR #1 Merged", table_cell),
            Paragraph(
                "&bull; <code>RecommendationService.java</code><br/>"
                "&bull; <code>ItineraryDAO.java</code><br/>"
                "&bull; <code>ItineraryService.java</code><br/>"
                "&bull; <code>RecommendationsPanel.java</code><br/>"
                "&bull; <code>ItineraryDialog.java</code><br/>"
                "&bull; <code>Dev4VerificationTest.java</code>",
                table_cell
            ),
            Paragraph(
                "Designed rule-based recommendation mathematical engine scoring packages (0-100%) across budget, "
                "duration, and category interest; built interactive preferences questionnaire with sliders and check chips; "
                "built day-by-day itinerary schedule retriever and timeline dialog view; wrote unit verification test suite.",
                table_cell
            )
        ],
        [
            Paragraph("<b>Garry George</b><br/>Dev 5: Booking & Payments", table_cell_bold),
            Paragraph("garry.george.5212@gmail.com<br/>Commit <code>1527a7f</code><br/>PR #3 Merged", table_cell),
            Paragraph(
                "&bull; <code>BookingDAO.java</code><br/>"
                "&bull; <code>BookingService.java</code><br/>"
                "&bull; <code>BookingException.java</code><br/>"
                "&bull; <code>PaymentMethod.java</code><br/>"
                "&bull; <code>CardPayment.java</code>, <code>UpiPayment.java</code><br/>"
                "&bull; <code>BookingDialog.java</code><br/>"
                "&bull; <code>BookingHistoryPanel.java</code>",
                table_cell
            ),
            Paragraph(
                "Engineered complete booking lifecycle; dynamic price calculator factoring guests and discounts; "
                "strict date validation preventing past bookings; payment strategy implementation for Card and UPI; "
                "48-hour cancellation policy with automated 80% refund computation; customer booking history dashboard "
                "with status pill tags and cancellation actions.",
                table_cell
            )
        ],
        [
            Paragraph("<b>Pius Varghese</b><br/>Dev 6: Admin Inventory CRUD", table_cell_bold),
            Paragraph("piusvarghese12@gmail.com<br/>Commit <code>b719bc8</code><br/>PR #6 Merged", table_cell),
            Paragraph(
                "&bull; <code>InventoryManagementPanel.java</code><br/>"
                "&bull; <code>BookingOversightPanel.java</code>",
                table_cell
            ),
            Paragraph(
                "Built full CRUD administrative panel for travel packages and destinations using JTable with "
                "modal Add/Edit/Delete dialogs and input validation; constructed platform-wide booking oversight "
                "monitoring table allowing administrators to inspect all customer transactions and status overrides.",
                table_cell
            )
        ],
        [
            Paragraph("<b>Ashwin V</b><br/>Dev 7: PDF Reports & Analytics", table_cell_bold),
            Paragraph("kunjachuv@gmail.com<br/>Commit <code>95db05d</code><br/>PR #4 Merged", table_cell),
            Paragraph(
                "&bull; <code>PDFExportService.java</code><br/>"
                "&bull; <code>AnalyticsService.java</code><br/>"
                "&bull; <code>ReportsPanel.java</code>",
                table_cell
            ),
            Paragraph(
                "Integrated OpenPDF (LGPL) to generate branded, printable customer itineraries and official tax invoices; "
                "authored SQL aggregation queries for destination popularity and net monthly revenues; built visual "
                "administrative analytics dashboard using JFreeChart (Bar Chart and Revenue Breakdown).",
                table_cell
            )
        ],
        [
            Paragraph("<b>Atul</b><br/>Dev 8: Reviews & Notifications", table_cell_bold),
            Paragraph("atul.moniv@gmail.com<br/>Commit <code>7a8b7ba</code><br/>PR #5 Merged", table_cell),
            Paragraph(
                "&bull; <code>ReviewDAO.java</code><br/>"
                "&bull; <code>ReviewDialog.java</code><br/>"
                "&bull; Notification banner in <code>CustomerMainFrame</code>",
                table_cell
            ),
            Paragraph(
                "Implemented customer package review and rating engine (1-5 stars) linked to confirmed trips; "
                "created interactive star-rating modal dialog; built in-app upcoming trip reminder banner alerting "
                "customers upon login if an active departure is within 7 days; visual rating star harmonizer.",
                table_cell
            )
        ]
    ]

    dev_table = Table(dev_data, colWidths=[CONTENT_WIDTH*0.22, CONTENT_WIDTH*0.22, CONTENT_WIDTH*0.26, CONTENT_WIDTH*0.30])
    dev_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), PRIMARY_DARK),
        ('ALIGN', (0,0), (-1,-1), 'LEFT'),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('GRID', (0,0), (-1,-1), 0.5, BORDER_COLOR),
        ('PADDING', (0,0), (-1,-1), 5),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [WHITE, BG_LIGHT])
    ]))
    story.append(dev_table)

    story.append(Spacer(1, 14))

    # =========================================================================
    # 4. CORE BUSINESS LOGIC & MATHEMATICAL FORMULAS
    # =========================================================================
    story.append(Paragraph("3. Core Business Logic, Algorithms & Mathematical Models", h1_style))
    story.append(Paragraph(
        "The application incorporates several specialized business logic algorithms across recommendation matching, "
        "cost estimation, cancellation refund guarantees, and cryptographic authentication:",
        body_style
    ))

    # 4.1 Recommendation Engine
    story.append(Paragraph("A. Rule-Based Recommendation Scoring Algorithm (RecommendationService)", h2_style))
    story.append(Paragraph(
        "Rather than relying on black-box heuristics, packages are scored transparently against user criteria using "
        "a weighted normalized multi-attribute utility model:",
        body_style
    ))

    rec_box = """<b>Scoring Formula:</b><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<b>FinalScore</b> = (0.40 &times; BudgetMatch + 0.35 &times; DurationMatch + 0.25 &times; InterestMatch) &times; 100<br/><br/>
<b>1. Budget Match Component (w = 0.40):</b><br/>
&bull; Estimated Trip Cost = (Package Final Price &times; Guests) + (Estimated Daily Cost &times; Duration &times; Guests)<br/>
&bull; If Estimated Cost &le; Budget: <code>BudgetMatch = 1.0 - (SavingsRatio &times; 0.25)</code> (Rewards affordability while utilizing budget).<br/>
&bull; If Estimated Cost &gt; Budget: <code>BudgetMatch = max(0.0, 1.0 - (Overspend / MaxTolerance))</code> where <code>MaxTolerance = Budget &times; 0.30</code>. A penalty is applied proportionally up to a 30% overspend cutoff, yielding 0 beyond that.<br/><br/>
<b>2. Duration Match Component (w = 0.35):</b><br/>
&bull; DiffDays = |PackageDuration - DesiredDays|<br/>
&bull; <code>DurationMatch = max(0.0, 1.0 - (DiffDays / DesiredDays))</code><br/><br/>
<b>3. Category / Interest Match Component (w = 0.25):</b><br/>
&bull; If user selects no preference: <code>InterestMatch = 0.70</code> (Neutral).<br/>
&bull; If category matches user selection: <code>InterestMatch = 1.00</code>.<br/>
&bull; If category does not match: <code>InterestMatch = 0.15</code>."""
    rec_table = Table([[Paragraph(rec_box, callout_text)]], colWidths=[CONTENT_WIDTH])
    rec_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), BG_LIGHT),
        ('BOX', (0,0), (-1,-1), 1, PRIMARY),
        ('PADDING', (0,0), (-1,-1), 8),
    ]))
    story.append(rec_table)
    story.append(Spacer(1, 8))

    # 4.2 Cost Calculation and Cancellation Refund
    story.append(Paragraph("B. Pricing Calculation & 48-Hour Cancellation Refund Policy (BookingService)", h2_style))
    price_box = """<b>1. Dynamic Price Calculation:</b><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<code>Total = (BasePrice &times; Guests) &times; (1.0 - DiscountPercentage / 100.0)</code><br/>
&nbsp;&nbsp;&nbsp;&nbsp;Guarantees exact 2-decimal rounded calculation for multi-guest groups.<br/><br/>
<b>2. Cancellation Refund Guarantee:</b><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<code>MinutesRemaining = Duration.between(Now, TravelDate.atStartOfDay()).toMinutes()</code><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&bull; If <code>MinutesRemaining &gt; 48 hours &times; 60 min</code>: <b>Refund Amount = TotalCost &times; 80%</b><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&bull; If <code>MinutesRemaining &le; 48 hours &times; 60 min</code>: <b>Refund Amount = Rs. 0.00 (0% refund)</b><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&bull; Trips already in the past (<code>TravelDate &lt; Now</code>) cannot be cancelled under any circumstance."""
    price_table = Table([[Paragraph(price_box, callout_text)]], colWidths=[CONTENT_WIDTH])
    price_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), BG_LIGHT),
        ('BOX', (0,0), (-1,-1), 1, ACCENT),
        ('PADDING', (0,0), (-1,-1), 8),
    ]))
    story.append(price_table)
    story.append(Spacer(1, 8))

    # 4.3 Security & Cryptographic Password Hashing
    story.append(Paragraph("C. Cryptographic Password Hashing (AuthService & jBCrypt)", h2_style))
    story.append(Paragraph(
        "Passwords are never stored in plaintext. During registration, <code>BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(12))</code> "
        "generates a salted one-way hash with a work factor of 12 (4,096 iterations). During login, "
        "<code>BCrypt.checkpw(enteredPassword, storedHash)</code> verifies credentials securely resisting rainbow table and timing attacks.",
        body_style
    ))

    story.append(Spacer(1, 14))

    # =========================================================================
    # 5. DATABASE SCHEMA & DATA DICTIONARY
    # =========================================================================
    story.append(Paragraph("4. Database Architecture & Relational Schema (MySQL)", h1_style))
    story.append(Paragraph(
        "The relational backend consists of 6 normalized tables hosted on MySQL 8.0, with foreign key integrity, "
        "cascade deletion constraints, and unique indexes:",
        body_style
    ))

    schema_data = [
        [
            Paragraph("Table Name", table_header),
            Paragraph("Primary Key", table_header),
            Paragraph("Key Columns & Types", table_header),
            Paragraph("Foreign Keys & Integrity Constraints", table_header)
        ],
        [
            Paragraph("<b>users</b>", table_cell_bold),
            Paragraph("<code>user_id</code> (INT AUTO)", table_cell),
            Paragraph("<code>full_name</code> VARCHAR(100), <code>email</code> VARCHAR(100) UNIQUE, <code>password_hash</code> VARCHAR(255), <code>role</code> ENUM('CUSTOMER', 'ADMIN'), <code>phone</code> VARCHAR(20)", table_cell),
            Paragraph("Unique index on <code>email</code> prevents duplicate accounts.", table_cell)
        ],
        [
            Paragraph("<b>destinations</b>", table_cell_bold),
            Paragraph("<code>destination_id</code> (INT AUTO)", table_cell),
            Paragraph("<code>name</code> VARCHAR(100), <code>country</code> VARCHAR(100), <code>description</code> TEXT, <code>category</code> ENUM('Adventure', 'Beach', 'Culture', 'Nature', 'Luxury'), <code>estimated_daily_cost</code> DECIMAL(10,2), <code>image_url</code> VARCHAR(255)", table_cell),
            Paragraph("Categorizes regions for recommendation filtering; maps local images in resources.", table_cell)
        ],
        [
            Paragraph("<b>travel_packages</b>", table_cell_bold),
            Paragraph("<code>package_id</code> (INT AUTO)", table_cell),
            Paragraph("<code>destination_id</code> INT, <code>title</code> VARCHAR(150), <code>duration_days</code> INT, <code>base_price</code> DECIMAL(10,2), <code>season</code> VARCHAR(50), <code>discount_percentage</code> DECIMAL(5,2), <code>is_active</code> BOOLEAN", table_cell),
            Paragraph("<code>FOREIGN KEY (destination_id) REFERENCES destinations(destination_id) ON DELETE CASCADE</code>", table_cell)
        ],
        [
            Paragraph("<b>itinerary_days</b>", table_cell_bold),
            Paragraph("<code>itinerary_id</code> (INT AUTO)", table_cell),
            Paragraph("<code>package_id</code> INT, <code>day_number</code> INT, <code>title</code> VARCHAR(150), <code>activity_description</code> TEXT", table_cell),
            Paragraph("<code>FOREIGN KEY (package_id) REFERENCES travel_packages(package_id) ON DELETE CASCADE</code>", table_cell)
        ],
        [
            Paragraph("<b>bookings</b>", table_cell_bold),
            Paragraph("<code>booking_id</code> (INT AUTO)", table_cell),
            Paragraph("<code>user_id</code> INT, <code>package_id</code> INT, <code>travel_date</code> DATE, <code>number_of_guests</code> INT, <code>total_cost</code> DECIMAL(10,2), <code>booking_status</code> ENUM('CONFIRMED', 'CANCELLED', 'COMPLETED'), <code>payment_status</code> VARCHAR(50), <code>refund_amount</code> DECIMAL(10,2)", table_cell),
            Paragraph("<code>FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE</code><br/><code>FOREIGN KEY (package_id) REFERENCES travel_packages(package_id) ON DELETE CASCADE</code>", table_cell)
        ],
        [
            Paragraph("<b>reviews</b>", table_cell_bold),
            Paragraph("<code>review_id</code> (INT AUTO)", table_cell),
            Paragraph("<code>user_id</code> INT, <code>package_id</code> INT, <code>rating</code> INT (1-5), <code>comment</code> TEXT, <code>review_date</code> TIMESTAMP", table_cell),
            Paragraph("<code>FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE</code><br/><code>CHECK (rating BETWEEN 1 AND 5)</code>", table_cell)
        ]
    ]

    schema_table = Table(schema_data, colWidths=[CONTENT_WIDTH*0.16, CONTENT_WIDTH*0.18, CONTENT_WIDTH*0.38, CONTENT_WIDTH*0.28])
    schema_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), PRIMARY_DARK),
        ('ALIGN', (0,0), (-1,-1), 'LEFT'),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('GRID', (0,0), (-1,-1), 0.5, BORDER_COLOR),
        ('PADDING', (0,0), (-1,-1), 5),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [WHITE, BG_LIGHT])
    ]))
    story.append(schema_table)

    story.append(Spacer(1, 14))

    # =========================================================================
    # 6. COMPLETE FILE-BY-FILE CODE CATALOG
    # =========================================================================
    story.append(Paragraph("5. Comprehensive File-by-File Catalog (What Each File Does)", h1_style))
    story.append(Paragraph(
        "Below is the complete inventory of all 48 source files, configuration classes, resources, and tests "
        "comprising the project. Each entry details the file's architectural role, core methods, dependencies, and design patterns:",
        body_style
    ))

    files_info = [
        # Config
        ("src/main/java/com/tourism/config/DBConnection.java", "Config / Database",
         "Thread-safe Singleton JDBC connection manager. Reads MySQL connection string (`jdbc:mysql://localhost:3306/tourism_db`), user (`tourism_user`), and password (`tourism123`). Contains reconnect logic and prevents connection leak by reusing the active connection instance."),
        
        ("src/main/java/com/tourism/config/ThemeConstants.java", "Config / UI Theme",
         "Centralized design system defining visual palette: Warm Sunset Coral (`#E05638`), Deep Terracotta (`#B2361C`), Emerald Green (`#27AE60`), Crimson (`#C0392B`), Charcoal Text (`#272B33`), Segoe UI font hierarchy (large header, subheaders, body, small), and padding standards."),
        
        ("src/main/java/com/tourism/config/CurrencyManager.java", "Config / Locale",
         "Thread-safe Singleton currency converter supporting USD, EUR, GBP, INR, JPY, AUD, CAD with dynamic exchange rates and symbol formatting. Implements the Observer pattern allowing UI widgets to subscribe to currency toggle events without restarting the application."),
        
        ("src/main/java/com/tourism/config/LanguageManager.java", "Config / i18n",
         "Localization manager supporting English, Spanish, French, German, Hindi, and Japanese translations for UI navigation labels, status banners, and system actions."),

        # Models
        ("src/main/java/com/tourism/model/User.java", "Model / Entity",
         "POJO encapsulating customer and administrator identity: `userId`, `fullName`, `email`, `passwordHash`, `role` (`CUSTOMER` vs `ADMIN`), `phone`, and `createdAt` timestamp."),

        ("src/main/java/com/tourism/model/Destination.java", "Model / Entity",
         "Entity representing geographic travel locations: `destinationId`, `name`, `country`, `description`, `category` (`Adventure`, `Beach`, `Culture`, `Nature`, `Luxury`), `estimatedDailyCost`, and image asset path."),

        ("src/main/java/com/tourism/model/Package.java", "Model / Entity",
         "Core travel package model: `packageId`, `destinationId`, `title`, `durationDays`, `basePrice`, `discountPercentage`, and `isActive`. Provides `getFinalPrice()` computing net discounted rate and optional references to hotel/transportation."),

        ("src/main/java/com/tourism/model/ItineraryDay.java", "Model / Entity",
         "Encapsulates individual day schedule for a package: `itineraryId`, `packageId`, `dayNumber`, `title`, and `activityDescription` detailing daily excursions."),

        ("src/main/java/com/tourism/model/Booking.java", "Model / Entity",
         "Represents customer reservation: `bookingId`, `userId`, `packageId`, `travelDate`, `numberOfGuests`, `totalCost`, `bookingStatus` (`CONFIRMED`, `CANCELLED`, `COMPLETED`), `paymentStatus`, and `refundAmount`."),

        ("src/main/java/com/tourism/model/Review.java", "Model / Entity",
         "Encapsulates customer feedback: `reviewId`, `userId`, `packageId`, numeric `rating` (1-5 stars), `comment` text, and `reviewDate`."),

        ("src/main/java/com/tourism/model/Hotel.java", "Model / Entity",
         "Represents lodging details: `hotelId`, `name`, `starRating`, `pricePerNight`, `address`, and amenity list."),

        ("src/main/java/com/tourism/model/Transportation.java", "Model / Entity",
         "Encapsulates transit options: `transportId`, `type` (Flight, Train, Luxury Bus), `provider`, `departureLocation`, `arrivalLocation`, and fare."),

        ("src/main/java/com/tourism/model/CustomerBookingDossier.java", "Model / Admin Composite",
         "Composite administrative entity joining customer registration details, package selection, travel date, guests, masked payment tokens, and fulfillment state for the Admin Dossier Inspector."),

        ("src/main/java/com/tourism/model/UserSession.java", "Model / Session",
         "Thread-safe Singleton holding the currently authenticated `User` object, tracking active login lifecycle and role permissions across the desktop session."),

        # DAOs
        ("src/main/java/com/tourism/dao/UserDAO.java", "DAO / Persistence",
         "Executes parameterized SQL queries for user authentication and management: `findByEmail()`, `registerUser()`, `findById()`, `emailExists()`, and `updateProfile()`."),

        ("src/main/java/com/tourism/dao/DestinationDAO.java", "DAO / Persistence",
         "CRUD operations for destinations: `findAll()`, `findById()`, `findByCategory()`, `insert()`, `update()`, and `delete()`."),

        ("src/main/java/com/tourism/dao/PackageDAO.java", "DAO / Persistence",
         "Package data layer: `findAll()`, `findAllActive()` (eagerly joining destination data), `findById()`, `searchPackages()`, and administrative CRUD operations (`createPackage`, `updatePackage`, `deletePackage`)."),

        ("src/main/java/com/tourism/dao/ItineraryDAO.java", "DAO / Persistence",
         "Itinerary persistence: `findByPackageId()` retrieving sorted day-by-day activities, `insert()`, and cascade deletion helpers."),

        ("src/main/java/com/tourism/dao/BookingDAO.java", "DAO / Persistence",
         "Booking database queries: `createBooking()`, `getUserBookings(userId)`, `getAllBookings()`, `cancelBooking(bookingId, refund)`, and `markPastTripsCompleted()` automated status updater."),

        ("src/main/java/com/tourism/dao/ReviewDAO.java", "DAO / Persistence",
         "Customer review persistence: `createReview()`, `findByPackageId()`, `getAverageRating()`, and `hasUserReviewedPackage()` preventing duplicate feedback."),

        ("src/main/java/com/tourism/dao/AdminDAO.java", "DAO / Persistence",
         "Specialized administrative audit queries joining customer rosters with booking fulfillment status, travel dates, and payment settlement updates (`updatePaymentStatus`, `updateBookingStatus`)."),

        # Services
        ("src/main/java/com/tourism/service/AuthService.java", "Service / Security",
         "Authentication business logic: performs input validation (email regex, password length), verifies passwords using `BCrypt.checkpw()`, and registers users with salted `BCrypt.hashpw()`."),

        ("src/main/java/com/tourism/service/RecommendationService.java", "Service / Algorithm",
         "Core rule-based recommendation engine. Implements the multi-factor scoring formula across budget, duration, and category interest, sorting packages in descending order with human-readable rationale."),

        ("src/main/java/com/tourism/service/ItineraryService.java", "Service / Workflow",
         "Manages itinerary retrieval, day sequencing, and dynamic synthesis of placeholder daily activities if customized schedules are not stored in database."),

        ("src/main/java/com/tourism/service/BookingService.java", "Service / Business Logic",
         "Booking orchestration: validates future dates and guest limits, calculates final discounted price, invokes payment strategy validation, creates booking records, and enforces the 48-hour 80% refund cancellation rule."),

        ("src/main/java/com/tourism/service/BookingException.java", "Service / Exception",
         "Domain-specific checked exception for invalid bookings, overcapacity, past dates, or payment failures."),

        ("src/main/java/com/tourism/service/PaymentMethod.java", "Service / Strategy",
         "Strategy design pattern interface declaring `validate()` and `processPayment(double amount, String reference)`."),

        ("src/main/java/com/tourism/service/CardPayment.java", "Service / Strategy",
         "Credit/Debit card payment strategy: validates 16-digit card number, MM/YY expiry, CVV, and produces masked receipt tokens (`•••• •••• •••• 1234`)."),

        ("src/main/java/com/tourism/service/UpiPayment.java", "Service / Strategy",
         "UPI payment strategy: validates Virtual Payment Address format (`user@bank`) via regular expression and generates unique transaction sequence IDs."),

        ("src/main/java/com/tourism/service/PDFExportService.java", "Service / PDF Engine",
         "OpenPDF document generator: constructs formatted, printable customer travel itineraries with day breakdown tables and official booking invoices with billing breakdown and customer details."),

        ("src/main/java/com/tourism/service/AnalyticsService.java", "Service / Analytics",
         "Administrative business intelligence service: aggregates non-cancelled bookings to compute top destinations and calculates net monthly revenue (`total_cost - refund_amount`) for JFreeChart rendering."),

        # UI Auth & Entry
        ("src/main/java/com/tourism/Main.java", "UI / Entry Point",
         "Application bootstrap class. Sets FlatLaf Look and Feel, verifies MySQL database connectivity, detects graphical display environment (`DISPLAY` / `WAYLAND_DISPLAY`), launches `LoginFrame` on the EDT, or prints helpful headless CLI diagnostics."),

        ("src/main/java/com/tourism/ui/auth/LoginFrame.java", "UI / Authentication",
         "Split-pane modern login window: left brand showcase with travel badges; right form with email/password fields, role detection, remember me, error alerts, and links to registration."),

        ("src/main/java/com/tourism/ui/auth/RegisterFrame.java", "UI / Authentication",
         "Customer registration modal with full name, email, phone, password, and confirmation validation; automatically hashes passwords and redirects to login."),

        # UI Customer
        ("src/main/java/com/tourism/ui/customer/CustomerMainFrame.java", "UI / Customer Portal",
         "Main customer dashboard hosting sidebar navigation tabs (Catalog, Recommendations, My Bookings), currency chooser, language chooser, light/dark theme toggle, user profile card, and 7-day upcoming trip reminder banner."),

        ("src/main/java/com/tourism/ui/customer/CatalogPanel.java", "UI / Customer Portal",
         "Interactive catalog browser featuring search bar, category quick-filter chips (Beach, Adventure, Culture, Nature, Luxury), budget filters, and a responsive grid layout of package cards."),

        ("src/main/java/com/tourism/ui/customer/PackageCard.java", "UI / Customer Portal",
         "Individual card widget displaying destination hero photo, title, duration pill badge, category chip, live weather badge, star rating, strike-through base price, discounted price, and action buttons."),

        ("src/main/java/com/tourism/ui/customer/RecommendationsPanel.java", "UI / Customer Portal",
         "Personalized questionnaire UI with sliders for budget, duration, guest count, and category check chips; triggers `RecommendationService` and renders ranked recommendation cards with match percentage tags."),

        ("src/main/java/com/tourism/ui/customer/ItineraryDialog.java", "UI / Customer Portal",
         "Modal dialog presenting day-wise timeline with destination photos, activity descriptions, and 'Download PDF Itinerary' button invoking `PDFExportService`."),

        ("src/main/java/com/tourism/ui/customer/BookingDialog.java", "UI / Customer Portal",
         "Modal booking dialog with date picker, guest spinner, live price recalculator, payment method tabs (Credit Card vs UPI), input validation, and booking confirmation confirmation."),

        ("src/main/java/com/tourism/ui/customer/BookingHistoryPanel.java", "UI / Customer Portal",
         "Customer reservation dashboard showing past and upcoming trips, status pill badges (`CONFIRMED`, `CANCELLED`, `COMPLETED`), cancellation button with real-time refund calculation, 'Download Invoice', and 'Boarding Pass' trigger."),

        ("src/main/java/com/tourism/ui/customer/BoardingPassDialog.java", "UI / Customer Portal",
         "Digital boarding pass modal styled like an airline flight ticket with route codes, passenger name, seat/gate info, perforation cutouts, and rendered barcode."),

        ("src/main/java/com/tourism/ui/customer/ReviewDialog.java", "UI / Customer Portal",
         "Interactive feedback dialog featuring 1-5 star rating selector, comment text area, and submission validation linked to completed trips."),

        # UI Admin
        ("src/main/java/com/tourism/ui/admin/AdminMainFrame.java", "UI / Admin Portal",
         "Administrative console navigation frame hosting Inventory Management, Booking Oversight, Customer Dossier & Settlements, and Analytics & Reports tabs with theme toggle and logout."),

        ("src/main/java/com/tourism/ui/admin/InventoryManagementPanel.java", "UI / Admin Portal",
         "Administrative package CRUD interface: searchable `JTable` of packages and destinations with Add, Edit, and Delete action dialogs and form validation."),

        ("src/main/java/com/tourism/ui/admin/BookingOversightPanel.java", "UI / Admin Portal",
         "Master booking oversight table displaying all platform transactions, guest counts, total revenue, status filters, and administrative cancellation override."),

        ("src/main/java/com/tourism/ui/admin/CustomerBookingsAdminPanel.java", "UI / Admin Portal",
         "Enterprise customer roster displaying registered travelers, selected travel packages, masked payment settlements, and trigger button for Dossier Inspector."),

        ("src/main/java/com/tourism/ui/admin/CustomerDossierDialog.java", "UI / Admin Portal",
         "Detailed modal inspector displaying comprehensive traveler identity, booked package metadata, itinerary timeline, and masked payment tokens."),

        ("src/main/java/com/tourism/ui/admin/ReportsPanel.java", "UI / Admin Portal",
         "Visual business intelligence dashboard incorporating JFreeChart: Top Destinations Bar Chart, Revenue Breakdown Pie Chart, and KPI metric summary cards."),

        # Resources & SQL
        ("src/main/resources/tourism_schema.sql", "Database / DDL",
         "MySQL database initialization script creating database, 6 tables with foreign keys, default administrative account (`admin@tourism.com`), sample customer (`john@gmail.com`), 11 global destinations, and day-by-day itineraries."),

        ("src/main/resources/images/*.jpg", "Assets / Media",
         "Curated high-resolution destination photographs for Bali, Swiss Alps, Kyoto, Paris, Kullu-Manali, Goa, Shimla, Kerala, Vietnam, Maldives, and Egypt."),

        # Tests
        ("src/test/java/com/tourism/VerifyAuth.java", "Test / Verification",
         "Automated unit test verifying BCrypt hash generation, password verification, and UserDAO authentication logic."),

        ("src/test/java/com/tourism/Dev3VerificationTest.java", "Test / Verification",
         "Unit test suite validating package catalog queries and active package filtering."),

        ("src/test/java/com/tourism/Dev4VerificationTest.java", "Test / Verification",
         "Algorithmic verification test asserting correct recommendation scoring, duration matching, and budget overspend penalties."),

        ("src/test/java/com/tourism/ui/customer/CatalogMockPreview.java", "Test / Harness",
         "Standalone Swing test harness for visual inspection of `PackageCard` layout, weather badges, and price formatting.")
    ]

    file_table_data = [
        [
            Paragraph("File Path & Class", table_header),
            Paragraph("Category", table_header),
            Paragraph("Architecture Role & Detailed Functionality", table_header)
        ]
    ]

    for fpath, cat, desc in files_info:
        fname = os.path.basename(fpath)
        pkg = os.path.dirname(fpath).replace("src/main/java/", "").replace("src/test/java/", "").replace("src/main/resources/", "")
        cell_path = f"<b>{fname}</b><br/><font color='#718096' size='7'>{pkg}</font>"
        file_table_data.append([
            Paragraph(cell_path, table_cell),
            Paragraph(f"<b>{cat}</b>", table_cell),
            Paragraph(desc, table_cell)
        ])

    files_table = Table(file_table_data, colWidths=[CONTENT_WIDTH*0.28, CONTENT_WIDTH*0.18, CONTENT_WIDTH*0.54])
    files_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), PRIMARY_DARK),
        ('ALIGN', (0,0), (-1,-1), 'LEFT'),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('GRID', (0,0), (-1,-1), 0.5, BORDER_COLOR),
        ('PADDING', (0,0), (-1,-1), 4),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [WHITE, BG_LIGHT])
    ]))
    story.append(files_table)

    story.append(Spacer(1, 14))

    # =========================================================================
    # 7. USER FLOWS & WORKFLOW WALKTHROUGHS
    # =========================================================================
    story.append(Paragraph("6. End-to-End System Workflows", h1_style))

    flows = [
        ("Customer Journey (From Discovery to Boarding Pass)", [
            "<b>1. Registration / Authentication:</b> User signs up in <code>RegisterFrame</code> with BCrypt-hashed password or logs in via <code>LoginFrame</code>; authenticated session is cached in <code>UserSession</code>.",
            "<b>2. Catalog Discovery & Recommendations:</b> User explores packages in <code>CatalogPanel</code> using category filter chips and search, or opens <code>RecommendationsPanel</code> to submit budget, duration, and style preferences to receive scored matches.",
            "<b>3. Day-by-Day Itinerary Inspection:</b> User clicks 'View Itinerary' on any <code>PackageCard</code> to inspect daily activities in <code>ItineraryDialog</code> and optionally clicks 'Download PDF Itinerary' to save an OpenPDF document.",
            "<b>4. Booking & Payment Processing:</b> User clicks 'Book Now', selects travel date and guest count; enters Credit Card or UPI credentials in <code>BookingDialog</code>; price is computed and verified before writing to MySQL.",
            "<b>5. Reservation Management & Boarding Pass:</b> User views bookings in <code>BookingHistoryPanel</code>, clicks 'Boarding Pass' to render digital pass with barcode in <code>BoardingPassDialog</code>, downloads official tax invoice PDF, or cancels with 80% refund if > 48 hours."
        ]),
        ("Administrator Journey (Inventory Management & Analytics)", [
            "<b>1. Secure Admin Access:</b> Admin signs in with verified <code>ADMIN</code> role, navigating directly to <code>AdminMainFrame</code>.",
            "<b>2. Inventory Control (CRUD):</b> In <code>InventoryManagementPanel</code>, admin creates, edits, discounts, or disables travel packages with real-time MySQL synchronization.",
            "<b>3. Booking Oversight & Status Overrides:</b> In <code>BookingOversightPanel</code>, admin reviews every platform booking and overrides reservation states when required.",
            "<b>4. Customer Dossier & Payment Settlements:</b> In <code>CustomerBookingsAdminPanel</code>, admin views customer roster, inspecting individual dossiers in <code>CustomerDossierDialog</code> with masked payment hashes.",
            "<b>5. Business Intelligence & Reporting:</b> In <code>ReportsPanel</code>, admin analyzes JFreeChart Top Destinations Bar Chart, monthly revenue trends, and overall booking volume KPIs."
        ])
    ]

    for title, steps in flows:
        story.append(Paragraph(title, h2_style))
        for step in steps:
            story.append(Paragraph(f"&bull; {step}", bullet_style))
        story.append(Spacer(1, 6))

    story.append(Spacer(1, 10))

    # =========================================================================
    # 8. BUILD, RUN & DEPLOYMENT INSTRUCTIONS
    # =========================================================================
    story.append(Paragraph("7. Build, Verification & Execution Reference", h1_style))
    commands_box = """<b>1. Prerequisites:</b> JDK 17 LTS, Apache Maven 3.8+, MySQL Server 8.0+ running on port 3306.<br/><br/>
<b>2. Database Setup:</b><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<code>CREATE USER IF NOT EXISTS 'tourism_user'@'localhost' IDENTIFIED BY 'tourism123';</code><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<code>CREATE DATABASE IF NOT EXISTS tourism_db;</code><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<code>GRANT ALL PRIVILEGES ON tourism_db.* TO 'tourism_user'@'localhost'; FLUSH PRIVILEGES;</code><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<code>mysql -u tourism_user -ptourism123 tourism_db &lt; src/main/resources/tourism_schema.sql</code><br/><br/>
<b>3. Maven Build & Desktop Execution:</b><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<code>mvn clean compile</code><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<code>mvn exec:java</code><br/><br/>
<b>4. Headless Terminal Fallback:</b><br/>
If executed over SSH or without a display server, <code>Main.java</code> gracefully detects lack of graphical environment, runs database diagnostics, and prints active destination/package counts without throwing unhandled exceptions."""
    cmd_table = Table([[Paragraph(commands_box, callout_text)]], colWidths=[CONTENT_WIDTH])
    cmd_table.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), BG_LIGHT),
        ('BOX', (0,0), (-1,-1), 1, BORDER_COLOR),
        ('PADDING', (0,0), (-1,-1), 8),
    ]))
    story.append(cmd_table)

    story.append(Spacer(1, 14))

    # Concluding signature
    story.append(HRFlowable(width="100%", thickness=1, color=PRIMARY, spaceBefore=10, spaceAfter=10))
    story.append(Paragraph(
        "<b>Document Prepared For:</b> Academic & Technical Review | <b>Architecture Status:</b> Fully Integrated & Tested (Java 17 / FlatLaf / MySQL 8.0)",
        ParagraphStyle('FooterNotice', parent=body_style, fontSize=8, textColor=TEXT_MUTED, alignment=1)
    ))

    # Build document
    doc.build(story, canvasmaker=NumberedCanvas)
    print(f"Successfully generated: {filename}")


if __name__ == '__main__':
    output_pdf = "Personalized_Tourism_Management_System_Complete_Code_Guide.pdf"
    if len(sys.argv) > 1:
        output_pdf = sys.argv[1]
    build_pdf(output_pdf)
