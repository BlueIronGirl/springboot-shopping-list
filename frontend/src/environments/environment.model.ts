export type Env = 'production' | 'staging' | 'develop'

export interface EnvironmentModel {
  mode: Env;
  webserviceurl: string;
}
