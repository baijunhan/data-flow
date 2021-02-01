import {Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {interval, Subscription, throwError} from 'rxjs';
import {catchError, map, switchMap} from 'rxjs/operators';
import {HttpClient} from '@angular/common/http';
import {AppService, wsServer} from '../../service/app.service';
import {DataSourceService} from '../../service/data-source.service';
import {CoreRateChartComponent} from './chart-component/core-rate-chart.component';
import {ReceiveMsgTotalChartComponent} from './chart-component/receive-msg-total-chart.component';
import {FlowStatisticsChartComponent} from './chart-component/flow-statistics-chart.component';
import {WebSocketSubject} from 'rxjs/internal-compatibility';
import {AppStatusData} from '../../model/app-status-data';

@Component({
  selector: 'statistics-template',
  template: `
    <div *ngIf="emptyData" style="width: 100%; height: 200px; display: flex; justify-content: center; align-items: center">
      <div style="display: flex; justify-content: flex-start; align-items: center">
        <svg t="1612193137535" class="icon" viewBox="0 0 1566 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="24021"
             width="128" height="128">
          <path
            d="M156.661991 699.757959h21.096999a10.443999 10.443999 0 0 1 10.235999 10.443999c0 5.765-4.491 10.443999-10.235999 10.444h-21.096999v21.097999a10.443999 10.443999 0 0 1-10.444 10.234999 10.276999 10.276999 0 0 1-10.443999-10.234999v-21.097999h-21.096999a10.443999 10.443999 0 0 1-10.234999-10.444c0-5.765 4.49-10.443999 10.234999-10.443999h21.096999v-21.096999a10.443999 10.443999 0 0 1 10.443999-10.234999c5.765 0 10.443999 4.49 10.444 10.234999v21.096999z m1378.627919-83.552995v-21.096999a10.276999 10.276999 0 0 0-10.443999-10.234999 10.443999 10.443999 0 0 0-10.444 10.234999v21.096999h-21.096998a10.276999 10.276999 0 0 0-10.235 10.443999c0 5.598 4.595 10.443999 10.235 10.444h21.096998v21.096998c0 5.745 4.679 10.235999 10.444 10.236a10.443999 10.443999 0 0 0 10.443999-10.236v-21.096998h21.097999c5.744 0 10.234999-4.679 10.234999-10.444a10.443999 10.443999 0 0 0-10.234999-10.443999h-21.097999zM776.459955 960.861944H250.596985a20.804999 20.804999 0 0 1-20.825998-20.887999c0-11.529999 9.462999-20.888999 20.825998-20.888999h94.727995a83.009995 83.009995 0 0 1-11.112-41.671997v-605.969965a83.489995 83.489995 0 0 1 83.636996-83.447995h62.580996v-20.992999a83.489995 83.489995 0 0 1 83.636995-83.448995h501.151971a83.448995 83.448995 0 0 1 83.636995 83.448995v605.969965c0 15.184999-4.053 29.409998-11.134 41.671997h115.553994c11.551999 0 20.909999 9.273999 20.909998 20.887999 0 11.529999-9.295999 20.887999-20.888998 20.887999h-250.659986v20.992999c0 15.185999-4.052 29.409998-11.132999 41.671997h11.195999c11.488999 0 20.825999 9.274999 20.825999 20.888999 0 11.529999-9.462999 20.887999-20.825999 20.887999H892.807948a41.657998 41.657998 0 0 1-6.413 50.862997 41.671998 41.671998 0 0 1-59.071996 0l-50.862997-50.862997z m76.367995-41.776998h66.423996c22.977999 0 41.609998-18.589999 41.609998-41.879997V270.460984c0-22.559999-18.047999-40.689998-40.313998-40.689997H416.303976c-22.266999 0-40.314998 18.213999-40.314998 40.689997v606.741965c0 23.123999 18.799999 41.880998 41.589998 41.880997h317.083981l-10.736999-10.756999a41.692998 41.692998 0 0 1-10.862-40.376998l-19.718999-19.739999a146.259991 146.259991 0 0 1-190.980988-220.516987 146.217991 146.217991 0 0 1 220.517987 190.980989l19.738998 19.739999a41.629998 41.629998 0 0 1 40.376998 10.839999l69.829996 69.829996z m149.809991-104.440994h62.852997a41.796998 41.796998 0 0 0 41.589997-41.776997v-605.759965c0-23.144999-18.632999-41.776998-41.589997-41.776997H563.774967a41.796998 41.796998 0 0 0-41.566998 41.775997v20.888999h396.793977a83.448995 83.448995 0 0 1 83.636995 83.448995v543.199968zM266.326984 46.998997h31.122999c8.773999 0 15.875999 6.955 15.875999 15.665999 0 8.647999-7.102 15.665999-15.875999 15.665999h-31.122999v31.123999c0 8.772999-6.956 15.874999-15.665999 15.874999a15.769999 15.769999 0 0 1-15.666999-15.874999V78.329995H203.869988a15.728999 15.728999 0 0 1-15.874999-15.665999c0-8.647999 7.102-15.665999 15.874999-15.665999h31.122998V15.874999C234.992986 7.102 241.949986 0 250.659985 0c8.646999 0 15.665999 7.102 15.665999 15.874999V46.999997zM20.887999 939.973945c0-11.529999 9.462999-20.888999 20.825999-20.888999h125.454992c11.488999 0 20.825999 9.274999 20.825999 20.888999 0 11.529999-9.462999 20.887999-20.825999 20.887999H41.713998a20.804999 20.804999 0 0 1-20.825999-20.887999z m658.733961-135.021992A104.441994 104.441994 0 1 0 531.899969 657.229961a104.441994 104.441994 0 0 0 147.721991 147.721992z m-220.079987-491.626971a20.887999 20.887999 0 0 1 20.867999-20.888999h229.791986a20.887999 20.887999 0 1 1 0 41.776997H480.430972a20.825999 20.825999 0 0 1-20.887999-20.887998z m0 104.440994c0-11.529999 9.295999-20.887999 20.742999-20.887999H814.789952c11.446999 0 20.741999 9.273999 20.741999 20.887999 0 11.529999-9.294999 20.887999-20.741999 20.887998H480.284972a20.762999 20.762999 0 0 1-20.741999-20.887998z m0 104.441993c0-11.529999 9.316999-20.888999 20.846999-20.888998h146.301991c11.509999 0 20.845999 9.274999 20.845999 20.888998 0 11.529999-9.315999 20.887999-20.845999 20.887999H480.388972a20.804999 20.804999 0 0 1-20.845999-20.887999zM62.665996 396.877977a62.664996 62.664996 0 1 1 0-125.329993 62.664996 62.664996 0 0 1 0 125.329993z m0-31.332998a31.331998 31.331998 0 1 0 0-62.664997 31.331998 31.331998 0 0 0 0 62.664997z m1295.074924-93.996995a62.664996 62.664996 0 1 1 0-125.329993 62.664996 62.664996 0 0 1 0 125.329993z m0-31.332998a31.331998 31.331998 0 1 0 0-62.663996 31.331998 31.331998 0 0 0 0 62.663996z"
            fill="#8A96A3" p-id="24022"></path>
        </svg>
        &nbsp;&nbsp;&nbsp;
        <span style="font-size: 18px; font-weight: bold">暂无消费记录</span>
      </div>
    </div>
    <div *ngIf="!emptyData">
      <div>
        <core-chart-rate-chart *ngIf="activeInstance" #rateChart></core-chart-rate-chart>
      </div>
      <div>
        <receive-msg-total-chart #receiveMsgTotalChart></receive-msg-total-chart>
      </div>
      <div>
        <flow-statistics-chart #flowStatisticsChart></flow-statistics-chart>
      </div>
    </div>

  `
})
export class StatisticsTemplateComponent implements OnInit, OnDestroy {

  constructor(public http: HttpClient,
              public app: AppService,
              public dataSource: DataSourceService) {
  }

  @ViewChild('rateChart')
  public rateChartComponent: CoreRateChartComponent;
  @ViewChild('receiveMsgTotalChart')
  public receiveMsgTotalChart: ReceiveMsgTotalChartComponent;
  @ViewChild('flowStatisticsChart')
  public flowStatisticChart: FlowStatisticsChartComponent;

  public interval: Subscription;

  @Input('instance-name')
  public instanceName: string = null;

  @Input('active-instance')
  public activeInstance: boolean = true;

  public emptyData = false;

  ngOnInit(): void {

    const url = `${wsServer}/monitor/status-data/${this.instanceName || '__all'}`;

    let lastAppStatusData = null;
    this.interval = new WebSocketSubject(url).pipe(
      catchError((err, caught) => {
        this.app.showSnackBar('ws connection failed!');
        return throwError(err);
      })
    ).subscribe((s: AppStatusData) => {

      if (AppStatusData.isEmpty(s)) {
        if(!this.emptyData) this.emptyData = true;
        return;
      } else if (this.emptyData) {
        this.emptyData = false;
      }

      if (this.activeInstance) {
        this.rateChartComponent && this.rateChartComponent.refreshChart(s, lastAppStatusData);
      }

      this.receiveMsgTotalChart && this.receiveMsgTotalChart.refreshChart(s);
      this.flowStatisticChart && this.flowStatisticChart.refreshChart(Object.assign(s));
      lastAppStatusData = s;
    });

  }

  ngOnDestroy(): void {
    this.interval.unsubscribe();
  }

}
