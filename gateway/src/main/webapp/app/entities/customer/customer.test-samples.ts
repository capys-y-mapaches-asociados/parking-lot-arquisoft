import { ICustomer, NewCustomer } from './customer.model';

export const sampleWithRequiredData: ICustomer = {
  id: 24379,
  firstName: 'Erin',
  lastName: 'White',
  email: 'pw8N@eqY.HGM4F.KtceO-.wgA3NS.qsWV.PvKF.Kw',
  password: '51fF3fb02AA0CCDe9736fcbBbFad49FC4246a639181dCaA85ccFcd482AfD0afb',
};

export const sampleWithPartialData: ICustomer = {
  id: 45998,
  firstName: 'Gregorio',
  lastName: 'Koepp',
  email: 'dgg@L9huw.EmeQ.VeVd.Te9EX.8.UKFB',
  password: 'eF576AFccec36F4B9e6a8b6Db7AfFe4bac174457BF6EAea8B82cdfA2D60aCa0c',
};

export const sampleWithFullData: ICustomer = {
  id: 28141,
  firstName: 'Junior',
  lastName: 'Gerhold',
  email: 'OmRIfc@RAnFCf.LQ.3.ratv3.A0P.o-IsZa.-YD',
  password: '6B0c7DAAE91E624943f6Eff5eF549A70d9fFd9C20DF5cFdAb90853Dd2cC9b949',
};

export const sampleWithNewData: NewCustomer = {
  firstName: 'Jennyfer',
  lastName: 'Schmeler',
  email: 'E5ft5@J4o.uiK',
  password: '82b3Da6dD12E77daC040C47802F68D26d0FD0fEF53Ea86843Fe7DcE0AcDc1Be9',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
