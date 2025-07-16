-- Phase 1: Core Financial Infrastructure Database Schema Extensions
-- File: src/main/resources/db/migration/V2__financial_infrastructure.sql

-- Supplier Management
CREATE TABLE suppliers (
                           supplier_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           supplier_name VARCHAR(255) NOT NULL,
                           company_name VARCHAR(255),
                           contact_person VARCHAR(255),
                           email VARCHAR(255),
                           phone VARCHAR(50),
                           address TEXT,
                           payment_terms VARCHAR(100),
                           tax_number VARCHAR(50),
                           is_active BOOLEAN DEFAULT TRUE,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Products/Parts Management
CREATE TABLE product_categories (
                                    category_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                    category_name VARCHAR(255) NOT NULL UNIQUE,
                                    description TEXT,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE products (
                          product_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          product_code VARCHAR(100) UNIQUE NOT NULL,
                          product_name VARCHAR(255) NOT NULL,
                          description TEXT,
                          category_id UUID REFERENCES product_categories(category_id),
                          brand VARCHAR(100),
                          unit_of_measure VARCHAR(50),
                          current_stock INTEGER DEFAULT 0,
                          minimum_stock INTEGER DEFAULT 0,
                          maximum_stock INTEGER DEFAULT 1000,
                          cost_price DECIMAL(12,2) DEFAULT 0.00,
                          selling_price DECIMAL(12,2) DEFAULT 0.00,
                          markup_percentage DECIMAL(5,2) DEFAULT 0.00,
                          supplier_id UUID REFERENCES suppliers(supplier_id),
                          is_active BOOLEAN DEFAULT TRUE,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Vehicle-Product Compatibility
CREATE TABLE product_vehicles (
                                  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                  product_id UUID NOT NULL REFERENCES products(product_id) ON DELETE CASCADE,
                                  vehicle_make VARCHAR(100) NOT NULL,
                                  vehicle_model VARCHAR(100),
                                  year_from INTEGER,
                                  year_to INTEGER,
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Inventory Transactions
CREATE TABLE inventory_transactions (
                                        transaction_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                        product_id UUID NOT NULL REFERENCES products(product_id),
                                        transaction_type VARCHAR(50) NOT NULL, -- 'PURCHASE', 'SALE', 'ADJUSTMENT', 'RETURN', 'TRANSFER'
                                        quantity INTEGER NOT NULL,
                                        unit_cost DECIMAL(12,2),
                                        total_amount DECIMAL(12,2),
                                        reference_type VARCHAR(50), -- 'JOB_CARD', 'PURCHASE_ORDER', 'ADJUSTMENT', 'MANUAL'
                                        reference_id UUID,
                                        transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        notes TEXT,
                                        created_by UUID, -- Reference to employee who created the transaction
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Quotations
CREATE TABLE quotations (
                            quotation_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            quotation_number VARCHAR(50) UNIQUE NOT NULL,
                            client_id UUID NOT NULL REFERENCES clients(client_id),
                            vehicle_id UUID REFERENCES vehicles(vehicle_id),
                            quotation_date DATE NOT NULL DEFAULT CURRENT_DATE,
                            valid_until DATE,
                            status VARCHAR(50) DEFAULT 'DRAFT', -- 'DRAFT', 'SENT', 'ACCEPTED', 'REJECTED', 'EXPIRED'
                            subtotal DECIMAL(12,2) DEFAULT 0.00,
                            tax_rate DECIMAL(5,2) DEFAULT 15.00,
                            tax_amount DECIMAL(12,2) DEFAULT 0.00,
                            discount_percentage DECIMAL(5,2) DEFAULT 0.00,
                            discount_amount DECIMAL(12,2) DEFAULT 0.00,
                            total_amount DECIMAL(12,2) DEFAULT 0.00,
                            notes TEXT,
                            terms_and_conditions TEXT,
                            converted_to_job_card BOOLEAN DEFAULT FALSE,
                            job_card_id UUID REFERENCES jobcards(job_id),
                            created_by UUID, -- Reference to employee
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Quotation Line Items
CREATE TABLE quotation_items (
                                 item_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 quotation_id UUID NOT NULL REFERENCES quotations(quotation_id) ON DELETE CASCADE,
                                 product_id UUID REFERENCES products(product_id),
                                 description TEXT NOT NULL,
                                 quantity DECIMAL(10,2) NOT NULL DEFAULT 1,
                                 unit_price DECIMAL(12,2) NOT NULL,
                                 total_price DECIMAL(12,2) NOT NULL,
                                 item_type VARCHAR(50) NOT NULL, -- 'LABOR', 'PART', 'CONSUMABLE', 'SERVICE'
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Invoices
CREATE TABLE invoices (
                          invoice_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          invoice_number VARCHAR(50) UNIQUE NOT NULL,
                          job_card_id UUID REFERENCES jobcards(job_id),
                          quotation_id UUID REFERENCES quotations(quotation_id),
                          client_id UUID NOT NULL REFERENCES clients(client_id),
                          invoice_date DATE NOT NULL DEFAULT CURRENT_DATE,
                          due_date DATE,
                          subtotal DECIMAL(12,2) DEFAULT 0.00,
                          tax_rate DECIMAL(5,2) DEFAULT 15.00,
                          tax_amount DECIMAL(12,2) DEFAULT 0.00,
                          discount_percentage DECIMAL(5,2) DEFAULT 0.00,
                          discount_amount DECIMAL(12,2) DEFAULT 0.00,
                          total_amount DECIMAL(12,2) DEFAULT 0.00,
                          amount_paid DECIMAL(12,2) DEFAULT 0.00,
                          balance_due DECIMAL(12,2) DEFAULT 0.00,
                          status VARCHAR(50) DEFAULT 'DRAFT', -- 'DRAFT', 'SENT', 'PAID', 'PARTIALLY_PAID', 'OVERDUE', 'CANCELLED'
                          payment_terms VARCHAR(100),
                          notes TEXT,
                          created_by UUID, -- Reference to employee
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Invoice Line Items
CREATE TABLE invoice_items (
                               item_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               invoice_id UUID NOT NULL REFERENCES invoices(invoice_id) ON DELETE CASCADE,
                               product_id UUID REFERENCES products(product_id),
                               description TEXT NOT NULL,
                               quantity DECIMAL(10,2) NOT NULL DEFAULT 1,
                               unit_price DECIMAL(12,2) NOT NULL,
                               total_price DECIMAL(12,2) NOT NULL,
                               item_type VARCHAR(50) NOT NULL, -- 'LABOR', 'PART', 'CONSUMABLE', 'SERVICE'
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Payment Processing
CREATE TABLE payments (
                          payment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          invoice_id UUID NOT NULL REFERENCES invoices(invoice_id),
                          amount DECIMAL(12,2) NOT NULL,
                          payment_method VARCHAR(50) NOT NULL, -- 'CASH', 'CARD', 'BANK_TRANSFER', 'MOBILE_MONEY', 'CHEQUE'
                          payment_status VARCHAR(50) DEFAULT 'PENDING', -- 'PENDING', 'COMPLETED', 'FAILED', 'REFUNDED'
                          transaction_reference VARCHAR(255),
                          payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          reference_number VARCHAR(100),
                          notes TEXT,
                          processed_by UUID, -- Reference to employee who processed payment
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Appointments
CREATE TABLE appointments (
                              appointment_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              client_id UUID NOT NULL REFERENCES clients(client_id),
                              vehicle_id UUID NOT NULL REFERENCES vehicles(vehicle_id),
                              appointment_date DATE NOT NULL,
                              appointment_time TIME NOT NULL,
                              duration_minutes INTEGER DEFAULT 60,
                              service_type VARCHAR(255),
                              description TEXT,
                              status VARCHAR(50) DEFAULT 'SCHEDULED', -- 'SCHEDULED', 'CONFIRMED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'NO_SHOW'
                              reminder_sent BOOLEAN DEFAULT FALSE,
                              priority VARCHAR(20) DEFAULT 'NORMAL', -- 'LOW', 'NORMAL', 'HIGH', 'URGENT'
                              assigned_technician UUID REFERENCES employees(employee_id),
                              notes TEXT,
                              created_by UUID, -- Reference to employee who created appointment
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Expenses/Cost Tracking
CREATE TABLE expense_categories (
                                    category_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                    category_name VARCHAR(255) NOT NULL UNIQUE,
                                    description TEXT,
                                    is_active BOOLEAN DEFAULT TRUE,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE expenses (
                          expense_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          category_id UUID NOT NULL REFERENCES expense_categories(category_id),
                          description TEXT NOT NULL,
                          amount DECIMAL(12,2) NOT NULL,
                          expense_date DATE NOT NULL DEFAULT CURRENT_DATE,
                          receipt_number VARCHAR(100),
                          supplier_id UUID REFERENCES suppliers(supplier_id),
                          is_recurring BOOLEAN DEFAULT FALSE,
                          recurrence_period VARCHAR(20), -- 'MONTHLY', 'QUARTERLY', 'YEARLY'
                          notes TEXT,
                          created_by UUID, -- Reference to employee
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Financial Reports Configuration
CREATE TABLE report_templates (
                                  template_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                  template_name VARCHAR(255) NOT NULL,
                                  report_type VARCHAR(100) NOT NULL, -- 'PROFIT_LOSS', 'EXPENSE', 'REVENUE', 'INVENTORY', 'AGED_RECEIVABLES'
                                  configuration JSONB,
                                  is_active BOOLEAN DEFAULT TRUE,
                                  created_by UUID, -- Reference to employee
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_supplier ON products(supplier_id);
CREATE INDEX idx_products_code ON products(product_code);
CREATE INDEX idx_inventory_transactions_product ON inventory_transactions(product_id);
CREATE INDEX idx_inventory_transactions_date ON inventory_transactions(transaction_date);
CREATE INDEX idx_quotations_client ON quotations(client_id);
CREATE INDEX idx_quotations_vehicle ON quotations(vehicle_id);
CREATE INDEX idx_quotations_date ON quotations(quotation_date);
CREATE INDEX idx_quotations_status ON quotations(status);
CREATE INDEX idx_invoices_client ON invoices(client_id);
CREATE INDEX idx_invoices_job_card ON invoices(job_card_id);
CREATE INDEX idx_invoices_date ON invoices(invoice_date);
CREATE INDEX idx_invoices_status ON invoices(status);
CREATE INDEX idx_payments_invoice ON payments(invoice_id);
CREATE INDEX idx_payments_date ON payments(payment_date);
CREATE INDEX idx_payments_status ON payments(payment_status);
CREATE INDEX idx_appointments_client ON appointments(client_id);
CREATE INDEX idx_appointments_vehicle ON appointments(vehicle_id);
CREATE INDEX idx_appointments_date ON appointments(appointment_date);
CREATE INDEX idx_appointments_status ON appointments(status);

-- Insert default data
INSERT INTO product_categories (category_name, description) VALUES
                                                                ('Engine Parts', 'Components related to engine functionality'),
                                                                ('Brake System', 'Brake pads, discs, and related components'),
                                                                ('Electrical', 'Electrical components and accessories'),
                                                                ('Fluids & Lubricants', 'Oils, coolants, and other fluids'),
                                                                ('Filters', 'Air, oil, fuel, and cabin filters'),
                                                                ('Tires & Wheels', 'Tires, rims, and wheel accessories'),
                                                                ('Suspension', 'Shock absorbers, springs, and suspension components'),
                                                                ('Exhaust System', 'Mufflers, pipes, and exhaust components'),
                                                                ('Interior', 'Seats, carpets, and interior accessories'),
                                                                ('Exterior', 'Body panels, lights, and exterior accessories');

INSERT INTO expense_categories (category_name, description) VALUES
                                                                ('Utilities', 'Electricity, water, internet, and other utility bills'),
                                                                ('Rent', 'Workshop and office rent expenses'),
                                                                ('Equipment', 'Tools, machinery, and equipment purchases'),
                                                                ('Marketing', 'Advertising and promotional expenses'),
                                                                ('Insurance', 'Business and vehicle insurance premiums'),
                                                                ('Training', 'Employee training and certification costs'),
                                                                ('Travel', 'Business travel and transportation expenses'),
                                                                ('Office Supplies', 'Stationery, printing, and office materials'),
                                                                ('Professional Services', 'Legal, accounting, and consulting fees'),
                                                                ('Maintenance', 'Building and equipment maintenance costs');

-- Add triggers for automatic timestamp updates
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for tables with updated_at columns
CREATE TRIGGER update_suppliers_updated_at BEFORE UPDATE ON suppliers FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_products_updated_at BEFORE UPDATE ON products FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_quotations_updated_at BEFORE UPDATE ON quotations FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_invoices_updated_at BEFORE UPDATE ON invoices FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_payments_updated_at BEFORE UPDATE ON payments FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_appointments_updated_at BEFORE UPDATE ON appointments FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();