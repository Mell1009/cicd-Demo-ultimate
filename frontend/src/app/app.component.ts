import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../environments/environment';

interface Product {
  id: number;
  name: string;
  description: string;
  startingPrice: number;
  category: string;
  active: boolean;
}

@Component({
  selector: 'app-root',
  template: `
    <div class="container">
      <header>
        <h1>🏛️ Online Auction</h1>
        <p class="subtitle">CI/CD Demo — GitHub Actions + Spring Boot + Angular</p>
      </header>

      <div class="status-bar" [class.connected]="connected" [class.disconnected]="!connected">
        <span>Backend: {{ connected ? '✅ Connected' : '❌ Disconnected' }}</span>
      </div>

      <section class="products">
        <h2>Active Lots</h2>
        <div class="grid">
          <div class="card" *ngFor="let product of products">
            <span class="badge">{{ product.category }}</span>
            <h3>{{ product.name }}</h3>
            <p>{{ product.description }}</p>
            <div class="price">
              Starting at <strong>\${{ product.startingPrice | number:'1.2-2' }}</strong>
            </div>
          </div>
        </div>
        <p *ngIf="products.length === 0 && !loading" class="empty">No active lots found.</p>
        <p *ngIf="loading" class="loading">Loading...</p>
      </section>
    </div>
  `,
  styles: [`
    * { box-sizing: border-box; margin: 0; padding: 0; }
    body { font-family: 'Segoe UI', sans-serif; background: #f0f2f5; }
    .container { max-width: 900px; margin: 0 auto; padding: 2rem; }
    header { text-align: center; margin-bottom: 1.5rem; }
    h1 { font-size: 2rem; color: #1a1a2e; }
    .subtitle { color: #666; margin-top: 0.5rem; font-size: 0.9rem; }
    .status-bar { padding: 0.5rem 1rem; border-radius: 6px; margin-bottom: 1.5rem;
                  font-size: 0.85rem; text-align: center; }
    .connected { background: #d4edda; color: #155724; }
    .disconnected { background: #f8d7da; color: #721c24; }
    h2 { margin-bottom: 1rem; color: #333; }
    .grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); gap: 1rem; }
    .card { background: white; border-radius: 10px; padding: 1.2rem;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08); position: relative; }
    .badge { position: absolute; top: 1rem; right: 1rem; background: #6c63ff;
             color: white; padding: 0.2rem 0.6rem; border-radius: 20px; font-size: 0.75rem; }
    h3 { font-size: 1.1rem; margin-bottom: 0.4rem; color: #222; }
    p { color: #666; font-size: 0.9rem; line-height: 1.4; }
    .price { margin-top: 1rem; color: #333; }
    .price strong { color: #6c63ff; font-size: 1.1rem; }
    .empty, .loading { color: #888; text-align: center; padding: 2rem; }
  `]
})
export class AppComponent implements OnInit {
  products: Product[] = [];
  connected = false;
  loading = true;

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.http.get<Product[]>(`${environment.apiUrl}/v1/api/products?activeOnly=true`)
      .subscribe({
        next: (data) => {
          this.products = data;
          this.connected = true;
          this.loading = false;
        },
        error: () => {
          this.connected = false;
          this.loading = false;
        }
      });
  }
}
